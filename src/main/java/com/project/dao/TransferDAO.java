package com.project.dao;

import com.project.entity.Transfer;
import com.project.util.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransferDAO {

    public Transfer insertNewTransfer(Transfer transfer, String direction){
        String selectSql = "SELECT balance FROM account WHERE id = ?";
        String updateSql = "UPDATE account SET balance = ? WHERE id = ?";
        String insertSql = "INSERT INTO transfer (charge_amount, remaining_balance, previous_balance, account_id, user_id, date) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                selectPs.setInt(1, transfer.getAccountId());
                ResultSet rs = selectPs.executeQuery();

                if (rs.next()) {
                    double currentbalance = Transfer.roundToTwoDecimalPlaces(rs.getDouble("balance"));
                    double newBalance = 0;
                    if (direction.equals("deposit")) {
                        double tmp = Transfer.roundToTwoDecimalPlaces(currentbalance + transfer.getChargeAmount());
                        newBalance = tmp;
                    } else if (direction.equals("withdrawal")){
                        double tmp = Transfer.roundToTwoDecimalPlaces(currentbalance - transfer.getChargeAmount());
                        newBalance = tmp;
                    }
                    double previousBalance = Transfer.roundToTwoDecimalPlaces(currentbalance); 

                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setDouble(1, newBalance);
                        updatePs.setInt(2, transfer.getAccountId());
                        updatePs.executeUpdate();
                    }

                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        if (direction.equals("deposit")){
                            insertPs.setDouble(1, Transfer.roundToTwoDecimalPlaces(transfer.getChargeAmount()));
                        } else if (direction.equals("withdrawal")){
                            insertPs.setDouble(1, Transfer.roundToTwoDecimalPlaces(-transfer.getChargeAmount()));
                        }
                        // insertPs.setDouble(1, Transfer.roundToTwoDecimalPlaces(-transfer.getChargeAmount()));
                        // insertPs.setDouble(1, Transfer.roundToTwoDecimalPlaces(transfer.getChargeAmount()));
                        insertPs.setDouble(2, newBalance);
                        insertPs.setDouble(3, previousBalance);
                        insertPs.setInt(4, transfer.getAccountId());
                        insertPs.setInt(5, transfer.getUserId());
                        insertPs.setTimestamp(6, transfer.getDate());
                        insertPs.executeUpdate();

                        ResultSet generatedKeys = insertPs.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedTransferId = generatedKeys.getInt(1);
                            conn.commit();
                            return new Transfer(generatedTransferId, transfer.getDate(),
                                    transfer.getChargeAmount(), newBalance, previousBalance, transfer.getAccountId(), transfer.getUserId());
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error inserting transfer: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return null;
    }

    public Transfer selectTransferById(int id) {
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                
                    double chargeAmount = Transfer.roundToTwoDecimalPlaces(rs.getDouble("charge_amount"));
                    double remainingBalance = Transfer.roundToTwoDecimalPlaces(rs.getDouble("remaining_balance"));
                    double previousBalance = Transfer.roundToTwoDecimalPlaces(rs.getDouble("previous_balance"));
                    java.sql.Timestamp date = rs.getTimestamp("date");
                    int accountId = rs.getInt("account_id");
                    int userId = rs.getInt("user_id");


                    transfer = new Transfer(id, date,chargeAmount, remainingBalance, previousBalance, accountId, userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transfer with id " + id + ": " + e.getMessage());
        }

        return transfer;
    }

    public List<Transfer> selectTransfersByAccountId(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                double chargeAmount = Transfer.roundToTwoDecimalPlaces(rs.getDouble("charge_amount"));
                double remainingBalance = Transfer.roundToTwoDecimalPlaces(rs.getDouble("remaining_balance"));
                double previousBalance = Transfer.roundToTwoDecimalPlaces(rs.getDouble("previous_balance"));
                java.sql.Timestamp date = rs.getTimestamp("date");
                int userId = rs.getInt("user_id");

                Transfer transfer = new Transfer(id, date, chargeAmount, remainingBalance, previousBalance, accountId, userId);
                transfers.add(transfer);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transfers: " + e.getMessage());
        }

        return transfers;
    }

    public void deleteTransfersByAccountId(int accountId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM transfer WHERE account_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,accountId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " transfer(s).");
            System.out.println("");
            // ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteTransfersByUserId(int userId){
        try (Connection connection = DatabaseConnector.connect()){
            String sql = "DELETE FROM transfer WHERE user_id = ?";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setInt(1,userId);
            ps.executeUpdate();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
