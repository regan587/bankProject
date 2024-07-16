package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.project.entity.Account;
import com.project.util.DatabaseConnector;

public class AccountDAO {

    private TransferDAO transferDAO = new TransferDAO();

    public Account insertNewAccount(Account account) {
        String insertAccountSql = "INSERT INTO account (account_name, balance, user_id, interest_rate) VALUES (?,?,?,?)";
        String insertTransferSql = "INSERT INTO transfer (charge_amount, remaining_balance, previous_balance, account_id, user_id, date) VALUES (?,?,?,?,?,?)";
    
        Connection conn = null;
        PreparedStatement psAccount = null;
        PreparedStatement psTransfer = null;
    
        try {
            conn = DatabaseConnector.connect();
            conn.setAutoCommit(false);
    
            psAccount = conn.prepareStatement(insertAccountSql, PreparedStatement.RETURN_GENERATED_KEYS);
            psAccount.setString(1, account.getAccountName());
            psAccount.setDouble(2, Account.roundToTwoDecimalPlaces(account.getBalance()));
            psAccount.setInt(3, account.getUserId());
            if (account.getInterestRate() == 0){
                psAccount.setNull(4, java.sql.Types.DOUBLE);
            } else {
                psAccount.setDouble(4, Account.roundToTwoDecimalPlaces(account.getInterestRate()));
            }
            psAccount.executeUpdate();
    
            ResultSet rsAccount = psAccount.getGeneratedKeys();
            int generatedCheckingAccountId = -1;
    
            if (rsAccount.next()) {
                generatedCheckingAccountId = rsAccount.getInt(1);
            }
    
            if (generatedCheckingAccountId != -1) {
                psTransfer = conn.prepareStatement(insertTransferSql, PreparedStatement.RETURN_GENERATED_KEYS);
                psTransfer.setDouble(1, Account.roundToTwoDecimalPlaces(account.getBalance()));
                psTransfer.setDouble(2, Account.roundToTwoDecimalPlaces(account.getBalance()));
                psTransfer.setNull(3, java.sql.Types.DOUBLE);
                psTransfer.setInt(4, generatedCheckingAccountId);
                psTransfer.setInt(5, account.getUserId());
                psTransfer.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                psTransfer.executeUpdate();
    
                conn.commit();
    
                return new Account(generatedCheckingAccountId, account.getAccountName(), account.getBalance(), account.getInterestRate(),
                                    account.getUserId());
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transfer: " + rollbackEx.getMessage());
            }
            System.err.println("Error creating account: " + e.getMessage());
        } finally {
            try {
                if (psAccount != null) {
                    psAccount.close();
                }
                if (psTransfer != null) {
                    psTransfer.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources: " + closeEx.getMessage());
            }
        }
    
        return null;
    }

    public Timestamp getLatestTransferDate(int id) {
        String sql = "SELECT date FROM transfer WHERE account_id = ? ORDER BY date DESC LIMIT 1";
        Timestamp latestTransferDate = null;
    
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    latestTransferDate = rs.getTimestamp("date");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving latest transfer date for account with id " + id + ": " + e.getMessage());
        }
    
        return latestTransferDate;
    }

    public void updateAccountBalance(int id) {
        String sql = "UPDATE account SET balance = ? WHERE id = ?";
        Account account = selectAccountById(id);
        Timestamp latestTransferDate = getLatestTransferDate(account.getId());
        
        if (latestTransferDate == null) {
            System.err.println("No transfers found for account with id " + account.getId());
            return;
        }
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long differenceMillis = now.getTime() - latestTransferDate.getTime();
        long timeDifferenceMinutes = differenceMillis / (1000 * 60); // Correct conversion from milliseconds to minutes
        
        double currentBalance = account.getBalance();
        double interestRate = account.getInterestRate(); // Per minute interest rate
        
        // Apply compound interest formula
        double newBalance = currentBalance * Math.pow(1 + interestRate/ 60, timeDifferenceMinutes);
        
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, Account.roundToTwoDecimalPlaces(newBalance)); // Round to two decimal places
            ps.setInt(2, account.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating balance for account with id " + account.getId() + ": " + e.getMessage());
        }
    }
    

    public Account selectAccountById(int id){

        Account account = null;
        String sql = "SELECT * FROM account WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id); 
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String account_name = rs.getString("account_name");
                    double balance = Account.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                    double interest_rate = Account.roundToTwoDecimalPlaces(rs.getDouble("interest_rate"));
                    int user_id = rs.getInt("user_id");
                    
                    account = new Account(id, account_name, balance, interest_rate, user_id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with id " + id + ": " + e.getMessage());
        }
    
        return account;

    }

    public List<Account> selectAccountsByUserId(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String account_name = rs.getString("account_name");
                double balance = Account.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                double interest_rate = Account.roundToTwoDecimalPlaces(rs.getDouble("interest_rate"));
                
                Account account = new Account(id, account_name, balance, interest_rate, userId);
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
        }

        return accounts;
    }

    public Account selectAccountByAccountName(String accountName,int userId) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_name = ?";
    
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountName);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                int id = rs.getInt("id");
                double balance = Account.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                double interest_rate = Account.roundToTwoDecimalPlaces(rs.getDouble("interest_rate"));

                account = new Account(id,accountName,balance,interest_rate,userId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving account with account name " + accountName + ": " + e.getMessage());
        }
        return account;
    }
        
    public void deleteAccountByAccountId(int id){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM account WHERE id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,id);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transferDAO.deleteTransfersByAccountId(id);
    }

    public void deleteCheckingAccountsByUserId(int userId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM account WHERE user_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,userId);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transferDAO.deleteTransfersByUserId(userId);
    }

    public List<Account> selectAccountTypeTemplate(int userId , String sql) {
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String account_name = rs.getString("account_name");
                double balance = Account.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                double interest_rate = Account.roundToTwoDecimalPlaces(rs.getDouble("interest_rate"));
                
                Account account = new Account(id, account_name, balance, interest_rate, userId);
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
        }

        return accounts;
    }

    public List<Account> selectCheckingAccountsByUserId(int userId){
        String sql = "SELECT * FROM account WHERE user_id = ? and interest_rate IS NULL";
        return selectAccountTypeTemplate(userId, sql);
    }

    public List<Account> selectSavingAccountsByUserId(int userId){
        String sql = "SELECT * FROM account WHERE user_id = ? and interest_rate IS NOT NULL";
        return selectAccountTypeTemplate(userId, sql);
    }
    
}

