package com.project.service;

import java.util.List;
import com.project.dao.SavingAccountDAO;
import com.project.entity.SavingAccount;
import com.project.exception.SavingAccountBelowZeroException;
import com.project.exception.NoSavingAccountsException;
import com.project.exception.NullSavingAccountException;

public class SavingAccountService {
    
    private SavingAccountDAO savingAccountDAO;

    public SavingAccountService(){
        this.savingAccountDAO = new SavingAccountDAO();
    }

    public SavingAccountService(SavingAccountDAO savingAccountDAO){
        this.savingAccountDAO = savingAccountDAO;
    }

    public SavingAccount createNewSavingAccount(SavingAccount newSavingAccount){
        if (newSavingAccount.getBalance() < 0){
            throw new SavingAccountBelowZeroException("Cannot have less than 0 dollars in your account!");
        } else {
            System.out.println("");
            System.out.printf(newSavingAccount.getAccountName() + " Created with an initial deposit of: $%.2f! and an interest rate of: %.2f", newSavingAccount.getBalance(), newSavingAccount.getInterestRate());
            System.out.println("");
            return savingAccountDAO.insertNewSavingAccount(newSavingAccount);
        }
    }

    public boolean checkIfSavingAccountNameIsUnique(int userId, String username){
        List<SavingAccount> savingAccounts = savingAccountDAO.selectSavingAccountsByUserId(userId);
        for (SavingAccount savingAccount: savingAccounts){
            if (savingAccount.getAccountName().equals(username)){
                return false;
            }
        }
        return true;
    }

    public List<SavingAccount> viewSavingAccounts(int userId) {
        List<SavingAccount> savingAccounts = savingAccountDAO.selectSavingAccountsByUserId(userId);
        if (savingAccounts == null || savingAccounts.isEmpty()) {
            throw new NoSavingAccountsException("No checking accounts associated with this account!");
        }
        return savingAccounts;
    }

    public void formatSavingAccountListForSelection(int userId) {
        List<SavingAccount> savingAccounts = savingAccountDAO.selectSavingAccountsByUserId(userId);
        if (savingAccounts == null || savingAccounts.isEmpty()) {
            throw new NoSavingAccountsException("No checking accounts associated with this account!");
        }
        int count = 1;
        for (SavingAccount savingAccount : savingAccounts) {
            System.out.println(count + " " + savingAccount.getAccountName());
            count++;
        }
    }

    public SavingAccount selectSavingAccountByid(int id){
        SavingAccount savingAccount = savingAccountDAO.selectSavingAccountById(id);
        if (savingAccount == null){
            throw new NullSavingAccountException("Checking Account does not exist!");
        }  
        return savingAccountDAO.selectSavingAccountById(id);
    }

    public SavingAccount selectSavingAccountByAccountName(String accountName){
        SavingAccount savingAccount = savingAccountDAO.selectSavingAccountByAccountName(accountName);
        if (savingAccount == null){
            throw new NullSavingAccountException("Checking Account does not exist");
        }
        return savingAccountDAO.selectSavingAccountByAccountName(accountName);
    }

    public void deleteSavingAccount(int id) {
        SavingAccount savingAccount = savingAccountDAO.selectSavingAccountById(id);
        if (savingAccount == null) {
            throw new NoSavingAccountsException("Checking Account does not exist!");
        }
        System.out.println("Checking Account: " + savingAccount.getAccountName() + " deleted!");
        savingAccountDAO.deleteSavingAccountBySavingAccountId(id);
    }
    public void deleteSavingAccountsByUserId(int userId){
        List<SavingAccount> savingAccounts = savingAccountDAO.selectSavingAccountsByUserId(userId);
        if (savingAccounts.isEmpty()){
            throw new NoSavingAccountsException("There are no Checking Accounts to delete!");
        }
        savingAccountDAO.deleteSavingAccountsByUserId(userId);
        System.out.println("All accounts deleted");
    }


}
