package com.project.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import com.project.entity.CheckingAccount;
import com.project.entity.SavingAccount;
import com.project.exception.CheckingAccountBelowZeroException;
import com.project.exception.InvalidInputException;
import com.project.exception.NoCheckingAccountsException;
import com.project.exception.NoSavingAccountsException;
import com.project.exception.SavingAccountBelowZeroException;
import com.project.exception.DuplicateCheckingAccountNameException;
import com.project.exception.DuplicateSavingAccountNameException;
import com.project.service.CheckingAccountService;
import com.project.service.SavingAccountService;


public class CheckingAccountController {
    
    private CheckingAccountService checkingAccountService;
    private SavingAccountService savingAccountService;
    private Scanner scanner;

    public CheckingAccountController() {
        this.checkingAccountService = new CheckingAccountService();
        this.savingAccountService = new SavingAccountService();
        this.scanner = new Scanner(System.in);
    }

    public CheckingAccountController(CheckingAccountService checkingAccountService, SavingAccountService savingAccountService, Scanner scanner) {
        this.checkingAccountService = checkingAccountService;
        this.savingAccountService = savingAccountService;
        this.scanner = scanner;
    }

    public int[] takeUserInput(int userId) {
        while (true) {
            // System.out.println("""
            //         Please enter the number associated with the action you want to perform:
            //         1 Create New Checking Account
            //         2 Delete Checking Account
            //         3 View Checking Accounts
            //         4 Make a Deposit
            //         5 Make a Withdrawal
            //         6 Logout
            //         (Enter 'q' to exit at anytime);
            //         """);
            System.out.println("""
                    Please enter the number associated with the action you want to perform:
                    1 Create New Account
                    2 Delete Account
                    3 View Accounts
                    4 Make a Deposit
                    5 Make a Withdrawal
                    6 Logout
                    (Enter 'q' to exit at anytime);
                    """);
            String userInput = scanner.nextLine();  
            switch (userInput) {
                case "1":
                    System.out.println("""
                        Please enter the number associated with the action you want to perform:
                        1 Create New Checking Account
                        2 Create New Saving Account
                        """);
                    String choice = scanner.nextLine();
                    switch(choice){
                        case "1":
                            checkingAccountCreationHelper(userId);
                            break;

                        case "2":
                            savingAccountCreationHelper(userId);
                            break;
                    }
                    break;

                case "2":
                    checkingAccountDeletionHelper(userId);
                    break;

                case "3":
                    return checkingAccountViewHelper(userId);

                case "4":
                    return checkingAccountTransferHelper(userId, "Deposit");
                case "5":
                    return checkingAccountTransferHelper(userId, "Withdrawal");
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
                if (!checkingAccountService.checkIfACheckingAccountNameIsUnique(userId, newAccountName)){
                    throw new DuplicateCheckingAccountNameException("A checking account with that name already exists!");
                }
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

    private int[] checkingAccountTransferHelper(int userId, String direction) {
        
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

    private void savingAccountCreationHelper(int userId){
        try {
            try {
                System.out.println("Enter a name for your new account: ");
                String newAccountName = scanner.nextLine();
                if (!savingAccountService.checkIfSavingAccountNameIsUnique(userId, newAccountName)){
                    throw new DuplicateSavingAccountNameException("A checking account with that name already exists!");
                }
                System.out.println("Enter an amount to initially deposit into your new account: ");
                double newAccountAmount = Double.parseDouble(scanner.nextLine());
                SavingAccount savingAccount = new SavingAccount(newAccountName, newAccountAmount, savingAccountInterestRateGenerator(), userId);
                savingAccountService.createNewSavingAccount(savingAccount);
            } catch (SavingAccountBelowZeroException e){
                System.out.println(e.getMessage());
            }
        } catch (NumberFormatException e){
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void savingAccountDeletionHelper(int userId) {
        List<SavingAccount> savingAccounts = null;
        try{
            try {
                savingAccounts = savingAccountService.viewSavingAccounts(userId);
            } catch (NoSavingAccountsException e) {
                System.out.println(e.getMessage());
                return;  
            }
        
            System.out.println("Which account do you want to delete?");
            for (int i = 0; i < savingAccounts.size(); i++) {
                System.out.println((i + 1) + " " + savingAccounts.get(i).getAccountName());
            }

            String savingAccountChoice = scanner.nextLine();
        

            int accountIndex = Integer.parseInt(savingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < savingAccounts.size()) {
                SavingAccount selectedAccount = savingAccounts.get(accountIndex);
                System.out.println("");
                System.out.println("Selected Account: " + selectedAccount.getAccountName() + " | Total Balance: $" + selectedAccount.getBalance());
                System.out.println("Are you sure you want to delete this account? (yes/no): ");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("yes")) {
                    try {
                        savingAccountService.deleteSavingAccount(selectedAccount.getId());
                    } catch (NoSavingAccountsException e){
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
    

    private int[] savingAccountViewHelper(int userId) {
        try {
            try {
                savingAccountService.formatSavingAccountListForSelection(userId);
            } catch (NoSavingAccountsException e){
                System.out.println(e.getMessage());
                return new int[] {-1};
            }
            List<SavingAccount> savingAccounts = null;
            try {
                savingAccounts = savingAccountService.viewSavingAccounts(userId);
            } catch (NoSavingAccountsException e) {
                System.out.println(e.getMessage());
                return new int[] {-1}; 
            }
        
            if (savingAccounts == null || savingAccounts.isEmpty()) {
                System.out.println("No checking accounts found for the user!");
                return new int[] {-1}; 
            }
        
            System.out.println("Choose an account to view transfers!");
            String savingAccountChoice = scanner.nextLine();
        

            int accountIndex = Integer.parseInt(savingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < savingAccounts.size()) {
                SavingAccount selectedAccount = savingAccounts.get(accountIndex);
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

    private int[] savingAccountTransferHelper(int userId, String direction) {
        
        List<SavingAccount> savingAccounts = null;
        try {
            savingAccounts = savingAccountService.viewSavingAccounts(userId);
        } catch (NoSavingAccountsException e) {
            System.out.println(e.getMessage());
            return new int[]{-1};
        }
        if (direction.equals("Deposit")) {
            System.out.println("Which account do you want to deposit into?");
        } else if (direction.equals("Withdrawal")) {
            System.out.println("Which account do you want to withdraw from?");
        }
        for (int i = 0; i < savingAccounts.size(); i++) {
            System.out.println((i + 1) + " " + savingAccounts.get(i).getAccountName());
        }
        String savingAccountChoice = scanner.nextLine();
        try {
            int accountIndex = Integer.parseInt(savingAccountChoice) - 1;
            if (accountIndex >= 0 && accountIndex < savingAccounts.size()) {
                SavingAccount selectedAccount = savingAccounts.get(accountIndex);
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

    private double savingAccountInterestRateGenerator() {
        double random = Math.random();
        double maxRate = 10;
        return random * maxRate / 100;
    }


    public Double calculateInterest(SavingAccount savingsAccount, LocalDateTime lastInterestCalculation) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastInterestCalculation, now);
        double fiveMinuteIntervalsElapsed = duration.toDays() / 24 / 12;
        double balance = savingsAccount.getBalance();
        Double interest = balance * savingsAccount.getInterestRate() * fiveMinuteIntervalsElapsed;
        return interest;
        // BigDecimal interest = balance.multiply(savingsAccount.getInterestRate()).multiply(BigDecimal.valueOf(yearsElapsed));
        // balance = balance.add(interest).setScale(2, RoundingMode.HALF_UP);
    }
}


