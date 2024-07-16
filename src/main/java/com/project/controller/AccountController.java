package com.project.controller;

import java.util.List;
import java.util.Scanner;
import com.project.entity.Account;
import com.project.exception.CheckingAccountBelowZeroException;
import com.project.exception.InvalidInputException;
import com.project.exception.NoAccountsException;
import com.project.exception.DuplicateCheckingAccountNameException;
import com.project.service.AccountService;


public class AccountController {
    
    private AccountService accountService;
    private Scanner scanner;

    public AccountController() {
        this.accountService = new AccountService();
        this.scanner = new Scanner(System.in);
    }

    public AccountController(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    public int[] takeUserInput(int userId) {
        List<Account> savingAccounts = accountService.selectSavingAccounts(userId);
        for (Account savingAccount: savingAccounts){
            accountService.updateAccountBalance(savingAccount.getId());
        }
        while (true) {
            System.out.println("""
                    Please enter the number associated with the action you want to perform:
                    1 Create New Account
                    2 Delete Account
                    3 View Account Details
                    4 Make a Deposit
                    5 Make a Withdrawal
                    6 Logout
                    (Enter 'q' to exit at anytime);
                    """);
            
            String userInput = scanner.nextLine();  
            System.out.println("");
            switch (userInput) {
                case "1":
                    accountCreationInput(userId);
                    break;

                case "2": 
                    accountDeletionHelper(userId);
                    break;

                case "3":
                    return accountViewHelper(userId);

                case "4":
                    return accountTransferHelper(userId, "Deposit");
                case "5":
                    return accountTransferHelper(userId, "Withdrawal");
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

    private void accountCreationInput(int userId){
        System.out.println("""
                What type of account would you like to create?
                1 Checking Account
                2 Saving Account
                """);
        String userInput = scanner.nextLine();
        System.out.println("");
        switch (userInput) {
            case "1":
                accountCreationHelper(userId, false);
                break;
        
            case "2":
                accountCreationHelper(userId, true);
                break;
        }
    }

    private void accountCreationHelper(int userId, boolean isSavingAccount){
        try {
            try {
                System.out.println("Enter a name for your new account: ");
                String newAccountName = scanner.nextLine();
                if (!accountService.checkIfAnAccountNameIsUnique(userId, newAccountName)){
                    throw new DuplicateCheckingAccountNameException("A checking account with that name already exists!");
                }
                System.out.println("Enter an amount to initially deposit into your new account: ");
                double newAccountAmount = Double.parseDouble(scanner.nextLine());
                if (isSavingAccount){
                    double interestRate = generateInterestRate();
                    Account savingAccount = new Account(newAccountName, newAccountAmount, interestRate, userId);
                    accountService.createNewAccount(savingAccount);
                } else {
                    Account checkingAccount = new Account(newAccountName, newAccountAmount, userId);
                    accountService.createNewAccount(checkingAccount);
                }
            } catch (CheckingAccountBelowZeroException e){
                System.out.println(e.getMessage());
            }
        } catch (NumberFormatException e){
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void accountDeletionHelper(int userId) {
        List<Account> checkingAccounts = null;
        try{
            try {
                checkingAccounts = accountService.sortedAccountList(userId);
            } catch (NoAccountsException e) {
                System.out.println(e.getMessage());
                return;  
            }
        
            try {
                accountService.formatAccountListForSelection(userId);
                System.out.println("Enter the matching number of the account you want to delete");
                System.out.println("");
            } catch (NoAccountsException e){
                System.out.println(e.getMessage());
                return;
            }

            String checkingAccountChoice = scanner.nextLine();
        

            int accountIndex = Integer.parseInt(checkingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < checkingAccounts.size()) {
                Account selectedAccount = checkingAccounts.get(accountIndex);
                System.out.println("");
                System.out.println("Selected Account: " + selectedAccount.getAccountName() + " | Total Balance: $" + selectedAccount.getBalance());
                System.out.println("Are you sure you want to delete this account? (yes/no): ");
                System.out.println("");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("yes")) {
                    try {
                        accountService.deleteAccount(selectedAccount.getId());
                    } catch (NoAccountsException e){
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
    

    private int[] accountViewHelper(int userId) {
        try {
            try {
                accountService.formatAccountListForSelection(userId);
            } catch (NoAccountsException e){
                System.out.println(e.getMessage());
                return new int[] {-1};
            }
            List<Account> checkingAccounts = null;
            try {
                checkingAccounts = accountService.sortedAccountList(userId);
                System.out.println("Choose an account to view details!");
                System.out.println("");
            } catch (NoAccountsException e) {
                System.out.println(e.getMessage());
                return new int[] {-1}; 
            }
        
            // if (checkingAccounts == null || checkingAccounts.isEmpty()) {
            //     System.out.println("No checking accounts found for the user!");
            //     return new int[] {-1}; 
            // }
        
            // System.out.println("Choose an account to view transfers!");
            // System.out.println("");
            String checkingAccountChoice = scanner.nextLine();
            System.out.println("");
        

            int accountIndex = Integer.parseInt(checkingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < checkingAccounts.size()) {
                Account selectedAccount = checkingAccounts.get(accountIndex);
                if (selectedAccount.getInterestRate() > 0){
                    System.out.printf("Selected Account: " + selectedAccount.getAccountName() + 
                "  |  Total Balance: $%.2f  |  Interest Rate: %.2f", selectedAccount.getBalance(),selectedAccount.getInterestRate());
                } else {
                    System.out.printf("Selected Account: " + selectedAccount.getAccountName() + 
                "  |  Total Balance: $%.2f", selectedAccount.getBalance());
                }
                // System.out.printf("Selected Account: " + selectedAccount.getAccountName() + 
                // "  |  Total Balance: $%.2f", selectedAccount.getBalance());
                System.out.println("");
                int[] accountIdAndChoiceInt = {selectedAccount.getId(), 3};
                return accountIdAndChoiceInt;
            } 
        } catch (NumberFormatException e){
            System.out.println("Invalid input. Please enter a number.");
        }
        return new int[] {-1}; 
    }

    private int[] accountTransferHelper(int userId, String direction) {
        
        List<Account> checkingAccounts = null;
        try {
            checkingAccounts = accountService.sortedAccountList(userId);
        } catch (NoAccountsException e) {
            System.out.println(e.getMessage());
            return new int[]{-1};
        }
    
        try {
            accountService.formatAccountListForSelection(userId);
        } catch (NoAccountsException e){
            System.out.println(e.getMessage());
            return new int[]{-1};
        }

        if (direction.equals("Deposit")) {
            System.out.println("Which account do you want to deposit into?");
        } else if (direction.equals("Withdrawal")) {
            System.out.println("Which account do you want to withdraw from?");
        }
        System.out.println("");
        String checkingAccountChoice = scanner.nextLine();
        try {
            int accountIndex = Integer.parseInt(checkingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < checkingAccounts.size()) {
                Account selectedAccount = checkingAccounts.get(accountIndex);
                System.out.println("");
                System.out.printf("Selected Account: " + selectedAccount.getAccountName() + 
                "  |  Total Balance: $%.2f", selectedAccount.getBalance());
                System.out.println("");
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

    private Double generateInterestRate(){
        Double random = Math.random();
        Double maxRate = 10.0;
        Double fullRate = random * maxRate / 100;
        return fullRate;
    }

}