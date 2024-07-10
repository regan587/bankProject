package com.project.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.models.CheckingAccount;
import com.project.util.DatabaseConnector;

public class CheckingAccountDAO {

    private TransactionDAO transactionDAO = new TransactionDAO();

    public CheckingAccount insertNewCheckingAccount(CheckingAccount checkingAccount) {
        String insertAccountSql = "INSERT INTO checking_accounts (account_name, total_amount, user_id) VALUES (?,?,?)";
        String insertTransactionSql = "INSERT INTO transactions (description, charge_amount, total_amount_remaining, previous_amount, account_id, user_id, date) VALUES (?,?,?,?,?,?,?)";
    
        Connection conn = null;
        PreparedStatement psAccount = null;
        PreparedStatement psTransaction = null;
    
        try {
            conn = DatabaseConnector.connect();
            conn.setAutoCommit(false); // Set auto-commit to false
    
            // Inserting new checking account
            psAccount = conn.prepareStatement(insertAccountSql, PreparedStatement.RETURN_GENERATED_KEYS);
            psAccount.setString(1, checkingAccount.getAccountName());
            psAccount.setDouble(2, checkingAccount.getTotalAmount());
            psAccount.setInt(3, checkingAccount.getUserId());
            psAccount.executeUpdate();
    
            ResultSet rsAccount = psAccount.getGeneratedKeys();
            int generatedCheckingAccountId = -1;
    
            if (rsAccount.next()) {
                generatedCheckingAccountId = rsAccount.getInt(1);
            }
    
            // Inserting new transaction
            if (generatedCheckingAccountId != -1) {
                psTransaction = conn.prepareStatement(insertTransactionSql, PreparedStatement.RETURN_GENERATED_KEYS);
                psTransaction.setString(1, "Initial deposit"); // Description of the transaction
                psTransaction.setDouble(2, checkingAccount.getTotalAmount()); // Charge amount as initial deposit
                psTransaction.setDouble(3, checkingAccount.getTotalAmount()); // Total amount remaining after deposit
                psTransaction.setNull(4, java.sql.Types.DOUBLE); // Previous amount is null or 0 for initial deposit
                psTransaction.setInt(5, generatedCheckingAccountId); // Account ID
                psTransaction.setInt(6, checkingAccount.getUserId()); // User ID
                psTransaction.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis())); // Current timestamp
                psTransaction.executeUpdate();
    
                // Commit the transaction
                conn.commit();
    
                return new CheckingAccount(generatedCheckingAccountId, checkingAccount.getAccountName(),
                        checkingAccount.getTotalAmount(), checkingAccount.getUserId());
            }
    
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback in case of exception
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error creating checking account: " + e.getMessage());
        } finally {
            try {
                if (psAccount != null) {
                    psAccount.close();
                }
                if (psTransaction != null) {
                    psTransaction.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit to true after transaction
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources: " + closeEx.getMessage());
            }
        }
    
        return null;
    }
    

    public CheckingAccount selectCheckingAccountById(int id){

        CheckingAccount checkingAccount = null;
        String sql = "SELECT * FROM checking_accounts WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id); // Set the parameter for the placeholder
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String account_name = rs.getString("account_name");
                    Double total_amount = rs.getDouble("total_amount");
                    int user_id = rs.getInt("user_id");
                
                    checkingAccount = new CheckingAccount(id, account_name, total_amount, user_id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with id " + id + ": " + e.getMessage());
        }
    
        return checkingAccount;

    }

    public List<CheckingAccount> selectCheckingAccountsByUserId(int userId) {
        List<CheckingAccount> checkingAccounts = new ArrayList<>();
        String sql = "SELECT * FROM checking_accounts WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String accountName = rs.getString("account_name");
                double totalAmount = rs.getDouble("total_amount");

                CheckingAccount checkingAccount = new CheckingAccount(id, accountName, totalAmount, userId);
                checkingAccounts.add(checkingAccount);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving checking accounts: " + e.getMessage());
        }

        return checkingAccounts;
    }

    public CheckingAccount selectCheckingAccountByAccountName(String accountName) {
        CheckingAccount checkingAccount = null;
        String sql = "SELECT * FROM checking_accounts WHERE account_name = ?";
    
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountName);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                int id = rs.getInt("id");
                double totalAmount = rs.getDouble("total_amount");
                int userId = rs.getInt("user_id");
    
                checkingAccount = new CheckingAccount(id, accountName, totalAmount, userId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving checking account with account name " + accountName + ": " + e.getMessage());
        }
    
        return checkingAccount;
    }
    

    public void deleteCheckingAccountByCheckingAccountId(int id){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM checking_accounts WHERE id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,id);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transactionDAO.deleteTransactionsByCheckingAccountId(id);
    }

    public void deleteCheckingAccountsByUserId(int userId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM checking_accounts WHERE user_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,userId);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transactionDAO.deleteTransactionsByUserId(userId);
    }
    
}
