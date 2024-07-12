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

    private TransferDAO transferDAO = new TransferDAO();

    public CheckingAccount insertNewCheckingAccount(CheckingAccount checkingAccount) {
        String insertAccountSql = "INSERT INTO checking_account (account_name, balance, user_id) VALUES (?,?,?)";
        String insertTransferSql = "INSERT INTO transfer (charge_amount, remaining_balance, previous_balance, account_id, user_id, date) VALUES (?,?,?,?,?,?)";
    
        Connection conn = null;
        PreparedStatement psAccount = null;
        PreparedStatement psTransfer = null;
    
        try {
            conn = DatabaseConnector.connect();
            conn.setAutoCommit(false); 
    
            psAccount = conn.prepareStatement(insertAccountSql, PreparedStatement.RETURN_GENERATED_KEYS);
            psAccount.setString(1, checkingAccount.getAccountName());
            psAccount.setDouble(2, CheckingAccount.roundToTwoDecimalPlaces(checkingAccount.getBalance()));
            psAccount.setInt(3, checkingAccount.getUserId());
            psAccount.executeUpdate();
    
            ResultSet rsAccount = psAccount.getGeneratedKeys();
            int generatedCheckingAccountId = -1;
    
            if (rsAccount.next()) {
                generatedCheckingAccountId = rsAccount.getInt(1);
            }
    
            
            if (generatedCheckingAccountId != -1) {
                psTransfer = conn.prepareStatement(insertTransferSql, PreparedStatement.RETURN_GENERATED_KEYS);
                psTransfer.setDouble(1, CheckingAccount.roundToTwoDecimalPlaces(checkingAccount.getBalance())); 
                psTransfer.setDouble(2, CheckingAccount.roundToTwoDecimalPlaces(checkingAccount.getBalance())); 
                psTransfer.setNull(3, java.sql.Types.DOUBLE); 
                psTransfer.setInt(4, generatedCheckingAccountId); 
                psTransfer.setInt(5, checkingAccount.getUserId()); 
                psTransfer.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis())); 
                psTransfer.executeUpdate();
    
                
                conn.commit();
    
                return new CheckingAccount(generatedCheckingAccountId, checkingAccount.getAccountName(),
                        checkingAccount.getBalance(), checkingAccount.getUserId());
            }
    
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); 
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transfer: " + rollbackEx.getMessage());
            }
            System.err.println("Error creating checking account: " + e.getMessage());
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


    public CheckingAccount selectCheckingAccountById(int id){

        CheckingAccount checkingAccount = null;
        String sql = "SELECT * FROM checking_account WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id); 
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String account_name = rs.getString("account_name");
                    Double balance = CheckingAccount.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                    int user_id = rs.getInt("user_id");
                
                    checkingAccount = new CheckingAccount(id, account_name, balance, user_id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with id " + id + ": " + e.getMessage());
        }
    
        return checkingAccount;

    }

    public List<CheckingAccount> selectCheckingAccountsByUserId(int userId) {
        List<CheckingAccount> checkingAccounts = new ArrayList<>();
        String sql = "SELECT * FROM checking_account WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String accountName = rs.getString("account_name");
                double balance = CheckingAccount.roundToTwoDecimalPlaces(rs.getDouble("balance"));

                CheckingAccount checkingAccount = new CheckingAccount(id, accountName, balance, userId);
                checkingAccounts.add(checkingAccount);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving checking accounts: " + e.getMessage());
        }

        return checkingAccounts;
    }

    public CheckingAccount selectCheckingAccountByAccountName(String accountName) {
        CheckingAccount checkingAccount = null;
        String sql = "SELECT * FROM checking_account WHERE account_name = ?";
    
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountName);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                int id = rs.getInt("id");
                double balance = CheckingAccount.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                int userId = rs.getInt("user_id");
    
                checkingAccount = new CheckingAccount(id, accountName, balance, userId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving checking account with account name " + accountName + ": " + e.getMessage());
        }
    
        return checkingAccount;
    }
    

    public void deleteCheckingAccountByCheckingAccountId(int id){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM checking_account WHERE id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,id);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transferDAO.deleteTransfersByCheckingAccountId(id);
    }

    public void deleteCheckingAccountsByUserId(int userId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM checking_account WHERE user_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,userId);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transferDAO.deleteTransfersByUserId(userId);
    }
    
}
