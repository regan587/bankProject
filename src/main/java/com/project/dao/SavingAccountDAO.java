package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.project.entity.SavingAccount;
import com.project.util.DatabaseConnector;

public class SavingAccountDAO {

    private TransferDAO transferDAO = new TransferDAO();

    public SavingAccount insertNewSavingAccount(SavingAccount savingAccount) {
        String insertAccountSql = "INSERT INTO saving_account (account_name, balance, interest_rate, user_id) VALUES (?,?,?,?)";
        String insertTransferSql = "INSERT INTO transfer (charge_amount, remaining_balance, previous_balance, account_id, user_id, date) VALUES (?,?,?,?,?,?)";
    
        Connection conn = null;
        PreparedStatement psAccount = null;
        PreparedStatement psTransfer = null;
    
        try {
            conn = DatabaseConnector.connect();
            conn.setAutoCommit(false); 
    
            psAccount = conn.prepareStatement(insertAccountSql, PreparedStatement.RETURN_GENERATED_KEYS);
            psAccount.setString(1, savingAccount.getAccountName());
            psAccount.setDouble(2, SavingAccount.roundToTwoDecimalPlaces(savingAccount.getBalance()));
            psAccount.setDouble(3, savingAccount.getInterestRate());
            psAccount.setInt(4, savingAccount.getUserId());
            psAccount.executeUpdate();
    
            ResultSet rsAccount = psAccount.getGeneratedKeys();
            int generatedSavingAccountId = -1;
    
            if (rsAccount.next()) {
                generatedSavingAccountId = rsAccount.getInt(1);
            }
    
            
            if (generatedSavingAccountId != -1) {
                psTransfer = conn.prepareStatement(insertTransferSql, PreparedStatement.RETURN_GENERATED_KEYS);
                psTransfer.setDouble(1, SavingAccount.roundToTwoDecimalPlaces(savingAccount.getBalance())); 
                psTransfer.setDouble(2, SavingAccount.roundToTwoDecimalPlaces(savingAccount.getBalance())); 
                psTransfer.setNull(3, java.sql.Types.DOUBLE); 
                psTransfer.setInt(4, generatedSavingAccountId); 
                psTransfer.setInt(5, savingAccount.getUserId()); 
                psTransfer.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis())); 
                psTransfer.executeUpdate();
    
                
                conn.commit();
    
                return new SavingAccount(generatedSavingAccountId, savingAccount.getAccountName(),
                        savingAccount.getBalance(), savingAccount.getInterestRate(), savingAccount.getUserId());
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


    public SavingAccount selectSavingAccountById(int id){

        SavingAccount savingAccount = null;
        String sql = "SELECT * FROM saving_account WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id); 
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String account_name = rs.getString("account_name");
                    Double balance = SavingAccount.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                    Double interest_rate = rs.getDouble("interest_rate");
                    int user_id = rs.getInt("user_id");
                
                    savingAccount = new SavingAccount(id, account_name, balance, interest_rate, user_id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with id " + id + ": " + e.getMessage());
        }
    
        return savingAccount;

    }

    public List<SavingAccount> selectSavingAccountsByUserId(int userId) {
        List<SavingAccount> savingAccounts = new ArrayList<>();
        String sql = "SELECT * FROM saving_account WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String accountName = rs.getString("account_name");
                Double interest_rate = rs.getDouble("interest_rate");
                double balance = SavingAccount.roundToTwoDecimalPlaces(rs.getDouble("balance"));

                SavingAccount savingAccount = new SavingAccount(id, accountName, balance, interest_rate, userId);
                savingAccounts.add(savingAccount);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving checking accounts: " + e.getMessage());
        }

        return savingAccounts;
    }

    public SavingAccount selectSavingAccountByAccountName(String accountName) {
        SavingAccount savingAccount = null;
        String sql = "SELECT * FROM saving_account WHERE account_name = ?";
    
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountName);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                int id = rs.getInt("id");
                double balance = SavingAccount.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                Double interest_rate = rs.getDouble("interest_rate");
                int userId = rs.getInt("user_id");
    
                savingAccount = new SavingAccount(id, accountName, balance, interest_rate, userId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving checking account with account name " + accountName + ": " + e.getMessage());
        }
    
        return savingAccount;
    }
    

    public void deleteSavingAccountBySavingAccountId(int id){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM saving_account WHERE id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,id);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transferDAO.deleteTransfersBySavingAccountId(id);
    }

    public void deleteSavingAccountsByUserId(int userId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM saving_account WHERE user_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,userId);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        transferDAO.deleteTransfersByUserId(userId);
    }
    
}

