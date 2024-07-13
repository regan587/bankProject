package com.project.service;

import java.util.List;

import com.project.dao.TransferDAO;
import com.project.entity.Transfer;
import com.project.exception.CheckingAccountBelowZeroException;
import com.project.exception.TransferAmountBelowOneException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransferService {

    private TransferDAO transferDAO;

    public TransferService(){
        this.transferDAO = new TransferDAO();
    }

    public TransferService(TransferDAO transferDAO){
        this.transferDAO = transferDAO;
    }

    public Transfer createNewDeposit(Transfer transfer){
        Double chargeAmount = Transfer.roundToTwoDecimalPlaces(transfer.getChargeAmount());
        if (chargeAmount < 1){
            throw new TransferAmountBelowOneException("Deposit amount must be greater than 1 dollar!");
        }
        // return transferDAO.insertNewDepositTransfer(transfer);
        return transferDAO.insertNewTransfer(transfer, "deposit");
    }

    public Transfer createNewWithdrawal(Transfer transfer) {
        double chargeAmount = transfer.getChargeAmount();
        int accountId = transfer.getAccountId();
        List<Transfer> transfers = transferDAO.selectTransfersByAccountId(accountId);
        double currentbalance = transfers.get(transfers.size()-1).getRemainingBalance();
        if (chargeAmount < 1) {
            throw new TransferAmountBelowOneException("Withdrawal amount must be greater than 1 dollar!");
        }
    
        double newremainingBalance = currentbalance - chargeAmount;
    
        if (newremainingBalance < 0) {
            throw new CheckingAccountBelowZeroException("Checking account balances cannot go below 0!");
        }
    
        transfer.setRemainingBalance(newremainingBalance); // Update the transfer object with new total amount remaining
    
        // Proceed with inserting the withdrawal transfer into the database
        // return transferDAO.insertNewWithdrawalTransfer(transfer);
        return transferDAO.insertNewTransfer(transfer, "withdrawal");
    }

    // Unused possibly remove here and in dao
    public Transfer selectTransferById(int id){
        return transferDAO.selectTransferById(id);
    }

    public List<Transfer> selectTransfersByAccountId(int accountId){ 
        return transferDAO.selectTransfersByAccountId(accountId);
    }
    
    public void printTransfers(int accountId) {
        List<Transfer> transferList = transferDAO.selectTransfersByAccountId(accountId);
    
        // Define the date format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
        // Print the header
        System.out.printf("%-20s | %-27s | %s%n", "Charge Amount", "Total Amount Remaining", "Date");
    
        // Print the transfers
        for (Transfer transfer : transferList) {
            Timestamp timestamp = transfer.getDate(); // Assuming getDate() returns a Timestamp
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            String formattedDate = dateTime.format(outputFormatter);
    
            System.out.printf("%-20s | %-27s | %s%n", 
                            String.format("$%.2f", transfer.getChargeAmount()), 
                            String.format("$%.2f", transfer.getRemainingBalance()), 
                            formattedDate);
        }
    }
    
}
