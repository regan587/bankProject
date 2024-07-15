package com.project.controller;


import com.project.entity.Transfer;
import com.project.exception.CheckingAccountBelowZeroException;
import com.project.exception.TransferAmountBelowOneException;
import com.project.service.TransferService;

import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TransferController {
    
    private TransferService transferService;
    private Scanner scanner; 

    public TransferController(){
        this.transferService = new TransferService();
        this.scanner = new Scanner(System.in);
    }

    public TransferController(TransferService transferService, Scanner scanner){
        this.transferService = transferService;
        this.scanner = scanner;
    }

    public boolean takeUserInput(int accountId, String action){
        while (true){
            switch (action) {
                case "3":
                    transferService.printTransfers(accountId);
                    return true;

                case "4":
                    depositTransferHelper(accountId);
                    return true;
                
                case "5":
                    withdrawalTransferHelper(accountId);
                    return true;
                case "q":
                    return false;
            }
        }
    }

    private void depositTransferHelper(int accountId) {
        
        try {
            System.out.println("Enter an amount to Deposit into your account");
            double depositAmount = Transfer.roundToTwoDecimalPlaces(scanner.nextDouble());
            int depositUserId = transferService.selectTransfersByAccountId(accountId).get(0).getUserId();
            Transfer deposittransfer = new Transfer(depositAmount, accountId, depositUserId, new Timestamp(System.currentTimeMillis()));
            Transfer persistedtransfer = transferService.createNewDeposit(deposittransfer); 
            System.out.printf("$%.2f Has been Deposited into your account! Your new balance is: $%.2f%n", depositAmount, persistedtransfer.getRemainingBalance());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Clear the invalid input
        } catch (TransferAmountBelowOneException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    private void withdrawalTransferHelper(int accountId) {
        try {
            System.out.println("Enter an amount to withdraw from your account");
            double withdrawAmount = Transfer.roundToTwoDecimalPlaces(scanner.nextDouble());
            int withdrawUserId = transferService.selectTransfersByAccountId(accountId).get(0).getUserId();
            Transfer withdrawtransfer = new Transfer(withdrawAmount, accountId, withdrawUserId, new Timestamp(System.currentTimeMillis()));
            Transfer persistedtransfer = transferService.createNewWithdrawal(withdrawtransfer);
            System.out.println("");
            System.out.printf("$%.2f Has been withdrawn from your account! Your new balance is: $%.2f%n", withdrawAmount, persistedtransfer.getRemainingBalance());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Clear the invalid input
        } catch (TransferAmountBelowOneException | CheckingAccountBelowZeroException e) {
            System.out.println(e.getMessage());
        }
    }
}


