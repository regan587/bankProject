package com.project.services;

import java.util.List;

import com.project.daos.CheckingAccountDAO;
import com.project.exceptions.CheckingAccountBelowZeroException;
import com.project.exceptions.NoCheckingAccountsException;
import com.project.exceptions.NullCheckingAccountException;
import com.project.models.CheckingAccount;

public class CheckingAccountService {
    
    private CheckingAccountDAO checkingAccountDAO;

    public CheckingAccountService(){
        this.checkingAccountDAO = new CheckingAccountDAO();
    }

    public CheckingAccountService(CheckingAccountDAO checkingAccountDAO){
        this.checkingAccountDAO = checkingAccountDAO;
    }

    public CheckingAccount createNewCheckingAccount(CheckingAccount newCheckingAccount){
        if (newCheckingAccount.getTotalAmount() < 0){
            throw new CheckingAccountBelowZeroException("Cannot have less than 0 dollars in your account!");
        } else {
            System.out.println("");
            System.out.println(newCheckingAccount.getAccountName() + " Created with an initial deposit of: $" + newCheckingAccount.getTotalAmount() + "!");
            System.out.println("");
            return checkingAccountDAO.insertNewCheckingAccount(newCheckingAccount);
        }
    }

    public List<CheckingAccount> viewCheckingAccounts(int userId) {
        List<CheckingAccount> checkingAccounts = checkingAccountDAO.selectCheckingAccountsByUserId(userId);
        if (checkingAccounts == null || checkingAccounts.isEmpty()) {
            throw new NoCheckingAccountsException("No checking accounts associated with this account!");
        }
        return checkingAccounts;
    }

    public void formatCheckingAccountListForSelection(int userId) {
        List<CheckingAccount> checkingAccounts = checkingAccountDAO.selectCheckingAccountsByUserId(userId);
        if (checkingAccounts == null || checkingAccounts.isEmpty()) {
            throw new NoCheckingAccountsException("No checking accounts associated with this account!");
        }
        int count = 1;
        for (CheckingAccount checkingAccount : checkingAccounts) {
            System.out.println(count + " " + checkingAccount.getAccountName());
            count++;
        }
    }

    public CheckingAccount selectCheckingAccountByid(int id){
        CheckingAccount checkingAccount = checkingAccountDAO.selectCheckingAccountById(id);
        if (checkingAccount == null){
            throw new NullCheckingAccountException("Checking Account does not exist!");
        }  
        return checkingAccountDAO.selectCheckingAccountById(id);
    }

    public CheckingAccount selectCheckingAccountByAccountName(String accountName){
        CheckingAccount checkingAccount = checkingAccountDAO.selectCheckingAccountByAccountName(accountName);
        if (checkingAccount == null){
            throw new NullCheckingAccountException("Checking Account does not exist");
        }
        return checkingAccountDAO.selectCheckingAccountByAccountName(accountName);
    }

    public void deleteCheckingAccount(int id) {
        CheckingAccount checkingAccount = checkingAccountDAO.selectCheckingAccountById(id);
        if (checkingAccount == null) {
            throw new NoCheckingAccountsException("Checking Account does not exist!");
        }
        System.out.println("Checking Account: " + checkingAccount.getAccountName() + " deleted!");
        checkingAccountDAO.deleteCheckingAccountByCheckingAccountId(id);
    }
    public void deleteCheckingAccountsByUserId(int userId){
        List<CheckingAccount> checkingAccounts = checkingAccountDAO.selectCheckingAccountsByUserId(userId);
        if (checkingAccounts.isEmpty()){
            throw new NoCheckingAccountsException("There are no Checking Accounts to delete!");
        }
        checkingAccountDAO.deleteCheckingAccountsByUserId(userId);
        System.out.println("All accounts deleted");
    }


}
