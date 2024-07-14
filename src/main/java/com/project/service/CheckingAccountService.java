package com.project.service;

import java.util.List;
import com.project.dao.CheckingAccountDAO;
import com.project.entity.CheckingAccount;
import com.project.exception.CheckingAccountBelowZeroException;
import com.project.exception.NoCheckingAccountsException;
import com.project.exception.NullCheckingAccountException;

public class CheckingAccountService {
    
    private CheckingAccountDAO checkingAccountDAO;

    public CheckingAccountService(){
        this.checkingAccountDAO = new CheckingAccountDAO();
    }

    public CheckingAccountService(CheckingAccountDAO checkingAccountDAO){
        this.checkingAccountDAO = checkingAccountDAO;
    }

    public CheckingAccount createNewCheckingAccount(CheckingAccount newCheckingAccount){
        if (newCheckingAccount.getBalance() < 0){
            throw new CheckingAccountBelowZeroException("Cannot have less than 0 dollars in your account!");
        } else {
            System.out.println("");
            System.out.printf(newCheckingAccount.getAccountName() + " Created with an initial deposit of: $%.2f!", newCheckingAccount.getBalance());
            System.out.println("");
            return checkingAccountDAO.insertNewCheckingAccount(newCheckingAccount);
        }
    }

    public boolean checkIfACheckingAccountNameIsUnique(int userId, String username){
        List<CheckingAccount> checkingAccounts = checkingAccountDAO.selectCheckingAccountsByUserId(userId);
        for (CheckingAccount checkingAccount: checkingAccounts){
            if (checkingAccount.getAccountName().equals(username)){
                return false;
            }
        }
        return true;
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
