package com.project.controller;

import java.util.List;
import java.util.Scanner;

import com.project.entity.CheckingAccount;
import com.project.exception.CheckingAccountBelowZeroException;
import com.project.exception.InvalidInputException;
import com.project.exception.NoCheckingAccountsException;
import com.project.service.CheckingAccountService;


public class CheckingAccountController {
    
    private CheckingAccountService checkingAccountService;
    private Scanner scanner;

    public CheckingAccountController() {
        this.checkingAccountService = new CheckingAccountService();
        this.scanner = new Scanner(System.in);
    }

    public CheckingAccountController(CheckingAccountService checkingAccountService, Scanner scanner) {
        this.checkingAccountService = checkingAccountService;
        this.scanner = scanner;
    }

    public int[] takeUserInput(int userId) {
        while (true) {
            System.out.println("""
                    Please enter the number associated with the action you want to perform:
                    1 Create New Checking Account
                    2 Delete Checking Account
                    3 View Checking Accounts
                    4 Make a Deposit
                    5 Make a Withdrawal
                    6 Logout
                    (Enter 'q' to exit at anytime);
                    """);
            String userInput = scanner.nextLine();  
            switch (userInput) {
                case "1":
                    checkingAccountCreationHelper(userId);
                    break;

                case "2":
                    checkingAccountDeletionHelper(userId);
                    break;

                case "3":
                    return checkingAccountViewHelper(userId);

                case "4":
                    return checkingAccounttransferHelper(userId, "Deposit");
                case "5":
                    return checkingAccounttransferHelper(userId, "Withdrawal");
                case "6":
                    int[] logoutArray = {0,0};
                    return logoutArray;
                case "q":
                    return null;

                default:
                    throw new InvalidInputException("Invalid input: " + userInput);
            }
        }   
    }

    private void checkingAccountCreationHelper(int userId){
        try {
            try {
                System.out.println("Enter a name for your new account: ");
                String newAccountName = scanner.nextLine();
                System.out.println("Enter an amount to initially deposit into your new account: ");
                double newAccountAmount = Double.parseDouble(scanner.nextLine());
                CheckingAccount checkingAccount = new CheckingAccount(newAccountName, newAccountAmount , userId);
                checkingAccountService.createNewCheckingAccount(checkingAccount);
            } catch (CheckingAccountBelowZeroException e){
                System.out.println(e.getMessage());
            }
        } catch (NumberFormatException e){
            System.out.println("Invalid input. Please enter a number.");
        }
    
    }

    private void checkingAccountDeletionHelper(int userId) {
        List<CheckingAccount> checkingAccounts = null;
        try{
            try {
                checkingAccounts = checkingAccountService.viewCheckingAccounts(userId);
            } catch (NoCheckingAccountsException e) {
                System.out.println(e.getMessage());
                return;  
            }
        
            System.out.println("Which account do you want to delete?");
            for (int i = 0; i < checkingAccounts.size(); i++) {
                System.out.println((i + 1) + " " + checkingAccounts.get(i).getAccountName());
            }

            String checkingAccountChoice = scanner.nextLine();
        

            int accountIndex = Integer.parseInt(checkingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < checkingAccounts.size()) {
                CheckingAccount selectedAccount = checkingAccounts.get(accountIndex);
                System.out.println("");
                System.out.println("Selected Account: " + selectedAccount.getAccountName() + " | Total Balance: $" + selectedAccount.getBalance());
                System.out.println("Are you sure you want to delete this account? (yes/no): ");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("yes")) {
                    try {
                        checkingAccountService.deleteCheckingAccount(selectedAccount.getId());
                    } catch (NoCheckingAccountsException e){
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Account deletion cancelled.");
                }
            } 
        } catch (NumberFormatException e){
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    

    private int[] checkingAccountViewHelper(int userId) {
        try {
            try {
                checkingAccountService.formatCheckingAccountListForSelection(userId);
            } catch (NoCheckingAccountsException e){
                System.out.println(e.getMessage());
                return new int[] {-1};
            }
            List<CheckingAccount> checkingAccounts = null;
            try {
                checkingAccounts = checkingAccountService.viewCheckingAccounts(userId);
            } catch (NoCheckingAccountsException e) {
                System.out.println(e.getMessage());
                return new int[] {-1}; 
            }
        
            if (checkingAccounts == null || checkingAccounts.isEmpty()) {
                System.out.println("No checking accounts found for the user!");
                return new int[] {-1}; 
            }
        
            System.out.println("Choose an account to view transfers!");
            String checkingAccountChoice = scanner.nextLine();
        

            int accountIndex = Integer.parseInt(checkingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < checkingAccounts.size()) {
                CheckingAccount selectedAccount = checkingAccounts.get(accountIndex);
                System.out.printf("Selected Account: " + selectedAccount.getAccountName() + 
                "  |  Total Balance: $%.2f", selectedAccount.getBalance());
                System.out.println("");
                int[] accountIdAndChoiceInt = {selectedAccount.getId(), 3};
                return accountIdAndChoiceInt;
            } 
        } catch (NumberFormatException e){
            System.out.println("Invalid input. Please enter a number.");
        }
        return new int[] {-1}; 
    }

    private int[] checkingAccounttransferHelper(int userId, String direction) {
        
        List<CheckingAccount> checkingAccounts = null;
        try {
            checkingAccounts = checkingAccountService.viewCheckingAccounts(userId);
        } catch (NoCheckingAccountsException e) {
            System.out.println(e.getMessage());
            return new int[]{-1};
        }
        if (direction.equals("Deposit")) {
            System.out.println("Which account do you want to deposit into?");
        } else if (direction.equals("Withdrawal")) {
            System.out.println("Which account do you want to withdraw from?");
        }
        for (int i = 0; i < checkingAccounts.size(); i++) {
            System.out.println((i + 1) + " " + checkingAccounts.get(i).getAccountName());
        }
        String checkingAccountChoice = scanner.nextLine();
        try {
            int accountIndex = Integer.parseInt(checkingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < checkingAccounts.size()) {
                CheckingAccount selectedAccount = checkingAccounts.get(accountIndex);
                System.out.println("Selected Account: " + selectedAccount.getAccountName() + 
                "  |  Total Balance: " + selectedAccount.getBalance());
                int[] accountIdAndChoiceInt = new int[2];
                if (direction.equals("Deposit")) {
                    accountIdAndChoiceInt[0] = selectedAccount.getId();
                    accountIdAndChoiceInt[1] = 4;
                } else if (direction.equals("Withdrawal")) {
                    accountIdAndChoiceInt[0] = selectedAccount.getId();
                    accountIdAndChoiceInt[1] = 5;
                }
                return accountIdAndChoiceInt;
            } else {
                throw new InvalidInputException("Invalid account selection, please try again!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        } 
        return new int[] {-1};
    }
}
