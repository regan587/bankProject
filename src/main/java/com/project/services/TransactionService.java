package com.project.services;

import java.util.List;
import com.project.daos.TransactionDAO;
import com.project.exceptions.CheckingAccountBelowZeroException;
import com.project.exceptions.TransactionAmountBelowOneException;
import com.project.models.Transaction;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionService {

    private TransactionDAO transactionDAO;

    public TransactionService(){
        this.transactionDAO = new TransactionDAO();
    }

    public TransactionService(TransactionDAO transactionDAO){
        this.transactionDAO = transactionDAO;
    }

    public Transaction createNewDeposit(Transaction transaction){
        Double chargeAmount = transaction.getChargeAmount();
        if (chargeAmount < 1){
            throw new TransactionAmountBelowOneException("Deposit amount must be greater than 1 dollar!");
        }
        return transactionDAO.insertNewDepositTransaction(transaction);
    }

    public Transaction createNewWithdrawal(Transaction transaction) {
        double chargeAmount = transaction.getChargeAmount();
        int accountId = transaction.getAccountId();
        List<Transaction> transactions = transactionDAO.selectTransactionsByAccountId(accountId);
        double currentbalance = transactions.get(transactions.size()-1).getremainingBalance();
        if (chargeAmount < 1) {
            throw new TransactionAmountBelowOneException("Withdrawal amount must be greater than 1 dollar!");
        }
    
        double newremainingBalance = currentbalance - chargeAmount;
    
        if (newremainingBalance < 0) {
            throw new CheckingAccountBelowZeroException("Checking account balances cannot go below 0!");
        }
    
        transaction.setremainingBalance(newremainingBalance); // Update the transaction object with new total amount remaining
    
        // Proceed with inserting the withdrawal transaction into the database
        return transactionDAO.insertNewWithdrawalTransaction(transaction);
    }

    public Transaction selectTransactionById(int id){
        return transactionDAO.selectTransactionById(id);
    }

    public List<Transaction> selectTransactionsByAccountId(int accountId){ 
        return transactionDAO.selectTransactionsByAccountId(accountId);
    }
    
    public void printTransactions(int accountId) {
        List<Transaction> transactionList = transactionDAO.selectTransactionsByAccountId(accountId);
        
        // Define the date format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // Print the header
        System.out.printf("%-15s | %-22s | %s%n", "Charge Amount", "Total Amount Remaining", "Date");
        
        // Print the transactions
        for (Transaction transaction : transactionList) {
            Timestamp timestamp = transaction.getDate(); // Assuming getDate() returns a Timestamp
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            String formattedDate = dateTime.format(outputFormatter);
            
            System.out.printf("%-15.2f | %-22.2f | %s%n", 
                            transaction.getChargeAmount(), 
                            transaction.getremainingBalance(), 
                            formattedDate);
        }
    }
    
}
