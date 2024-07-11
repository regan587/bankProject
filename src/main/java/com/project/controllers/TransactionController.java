package com.project.controllers;


import com.project.exceptions.CheckingAccountBelowZeroException;
import com.project.exceptions.TransactionAmountBelowOneException;
import com.project.models.Transaction;
import com.project.services.TransactionService;
import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TransactionController {
    
    private TransactionService transactionService;
    private Scanner scanner;

    public TransactionController(){
        this.transactionService = new TransactionService();
        this.scanner = new Scanner(System.in);
    }

    public TransactionController(TransactionService transactionService, Scanner scanner){
        this.transactionService = transactionService;
        this.scanner = scanner;
    }

    public boolean takeUserInput(int accountId, String action){
        while (true){
            switch (action) {
                case "3":
                    transactionService.printTransactions(accountId);
                    return true;

                case "4":
                    depositTransactionHelper(accountId);
                    return true;
                
                case "5":
                    withdrawalTransactionHelper(accountId);
                    return true;
                case "q":
                    return false;
            }
        }
    }

    private void depositTransactionHelper(int accountId) {
        
        try {
            System.out.println("Enter an amount to Deposit into your account");
            double depositAmount = scanner.nextDouble();
            int depositUserId = transactionService.selectTransactionsByAccountId(accountId).get(0).getUserId();
            Transaction depositTransaction = new Transaction(depositAmount, accountId, depositUserId, new Timestamp(System.currentTimeMillis()));
            Transaction persistedTransaction = transactionService.createNewDeposit(depositTransaction); 
            System.out.println("");
            System.out.println(depositAmount + " Has been deposited into your account! Your new balance is: " + persistedTransaction.getremainingBalance());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Clear the invalid input
        } catch (TransactionAmountBelowOneException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    private void withdrawalTransactionHelper(int accountId) {
        try {
            System.out.println("Enter an amount to withdraw from your account");
            double withdrawAmount = scanner.nextDouble();
            int withdrawUserId = transactionService.selectTransactionsByAccountId(accountId).get(0).getUserId();
            Transaction withdrawTransaction = new Transaction(withdrawAmount, accountId, withdrawUserId, new Timestamp(System.currentTimeMillis()));
            Transaction persistedTransaction = transactionService.createNewWithdrawal(withdrawTransaction);
            System.out.println("");
            System.out.println(withdrawAmount + " Has been withdrawn from your account! Your new balance is: " + persistedTransaction.getremainingBalance());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Clear the invalid input
        } catch (TransactionAmountBelowOneException | CheckingAccountBelowZeroException e) {
            System.out.println(e.getMessage());
        }
    }
}


