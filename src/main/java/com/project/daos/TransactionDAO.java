package com.project.daos;

import com.project.models.Transaction;
import com.project.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public Transaction insertNewDepositTransaction(Transaction transaction) {
        String selectSql = "SELECT balance FROM checking_accounts WHERE id = ?";
        String updateSql = "UPDATE checking_accounts SET balance = ? WHERE id = ?";
        String insertSql = "INSERT INTO transactions (charge_amount, remaining_balance, previous_balance, account_id, user_id, date) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                selectPs.setInt(1, transaction.getAccountId());
                ResultSet rs = selectPs.executeQuery();

                if (rs.next()) {
                    double currentbalance = rs.getDouble("balance");
                    double newbalance = currentbalance + transaction.getChargeAmount();
                    double previousBalance = currentbalance; // Previous amount is current total amount before deduction

                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setDouble(1, newbalance);
                        updatePs.setInt(2, transaction.getAccountId());
                        updatePs.executeUpdate();
                    }

                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        insertPs.setDouble(1, transaction.getChargeAmount());
                        insertPs.setDouble(2, newbalance);
                        insertPs.setDouble(3, previousBalance);
                        insertPs.setInt(4, transaction.getAccountId());
                        insertPs.setInt(5, transaction.getUserId());
                        insertPs.setTimestamp(6, transaction.getDate()); // Assuming transaction.getDate() returns the timestamp
                        insertPs.executeUpdate();

                        ResultSet generatedKeys = insertPs.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedTransactionId = generatedKeys.getInt(1);
                            conn.commit();
                            return new Transaction(generatedTransactionId, transaction.getDate(),
                                    transaction.getChargeAmount(), newbalance, previousBalance, transaction.getAccountId(), transaction.getUserId());
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error inserting transaction: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return null;
    }

    public Transaction insertNewWithdrawalTransaction(Transaction transaction) {
        String selectSql = "SELECT balance FROM checking_accounts WHERE id = ?";
        String updateSql = "UPDATE checking_accounts SET balance = ? WHERE id = ?";
        String insertSql = "INSERT INTO transactions (charge_amount, remaining_balance, previous_balance, account_id, user_id, date) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                selectPs.setInt(1, transaction.getAccountId());
                ResultSet rs = selectPs.executeQuery();

                if (rs.next()) {
                    double currentbalance = rs.getDouble("balance");
                    double newbalance = currentbalance - transaction.getChargeAmount();
                    double previousBalance = currentbalance; // Previous amount is current total amount before deduction

                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setDouble(1, newbalance);
                        updatePs.setInt(2, transaction.getAccountId());
                        updatePs.executeUpdate();
                    }

                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        
                        insertPs.setDouble(1, -transaction.getChargeAmount());
                        insertPs.setDouble(2, newbalance);
                        insertPs.setDouble(3, previousBalance);
                        insertPs.setInt(4, transaction.getAccountId());
                        insertPs.setInt(5, transaction.getUserId());
                        insertPs.setTimestamp(6, transaction.getDate()); // Assuming transaction.getDate() returns the timestamp
                        insertPs.executeUpdate();

                        ResultSet generatedKeys = insertPs.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedTransactionId = generatedKeys.getInt(1);
                            conn.commit();
                            return new Transaction(generatedTransactionId, transaction.getDate(),
                                    transaction.getChargeAmount(), newbalance, previousBalance, transaction.getAccountId(), transaction.getUserId());
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error inserting transaction: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return null;
    }

    public Transaction selectTransactionById(int id) {
        Transaction transaction = null;
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // Set the parameter for the placeholder

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                
                    double chargeAmount = rs.getDouble("charge_amount");
                    double remainingBalance = rs.getDouble("remaining_balance");
                    double previousBalance = rs.getDouble("previous_balance");
                    java.sql.Timestamp date = rs.getTimestamp("date");
                    int accountId = rs.getInt("account_id");
                    int userId = rs.getInt("user_id");


                    transaction = new Transaction(id, date,chargeAmount, remainingBalance, previousBalance, accountId, userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction with id " + id + ": " + e.getMessage());
        }

        return transaction;
    }

    public List<Transaction> selectTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                double chargeAmount = rs.getDouble("charge_amount");
                double remainingBalance = rs.getDouble("remaining_balance");
                double previousBalance = rs.getDouble("previous_balance");
                java.sql.Timestamp date = rs.getTimestamp("date");
                int userId = rs.getInt("user_id");

                Transaction transaction = new Transaction(id, date, chargeAmount, remainingBalance, previousBalance, accountId, userId);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
        }

        return transactions;
    }

    public void deleteTransactionsByCheckingAccountId(int accountId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM transactions WHERE account_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,accountId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " transactions.");
            System.out.println("");
            // ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteTransactionsByUserId(int userId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM transactions WHERE user_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,userId);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
