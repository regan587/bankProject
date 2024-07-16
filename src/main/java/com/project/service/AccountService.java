package com.project.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.project.dao.AccountDAO;
import com.project.entity.Account;
import com.project.exception.CheckingAccountBelowZeroException;
import com.project.exception.NoAccountsException;
import com.project.exception.NoCheckingAccountsException;
import com.project.exception.NullCheckingAccountException;

public class AccountService {
    
    private AccountDAO accountDAO;

    public AccountService(){
        this.accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    public Account createNewAccount(Account newAccount){
        if (newAccount.getBalance() < 0){
            throw new CheckingAccountBelowZeroException("Cannot have less than 0 dollars in your account!");
        } else {
            System.out.println("");
            System.out.printf(newAccount.getAccountName() + " Created with an initial deposit of: $%.2f!", newAccount.getBalance());
            System.out.println("");
            System.out.println("");
            return accountDAO.insertNewAccount(newAccount);
        }
    }

    public boolean checkIfAnAccountNameIsUnique(int userId, String accountName){
        List<Account> accounts = accountDAO.selectAccountsByUserId(userId);
        for (Account account: accounts){
            if (account.getAccountName().equals(accountName)){
                return false;
            }
        }
        return true;
    }

    public List<Account> viewAccounts(int userId) {
        List<Account> accounts = accountDAO.selectAccountsByUserId(userId);
        if (accounts == null || accounts.isEmpty()) {
            throw new NoCheckingAccountsException("You have not created an account yet!");
        }
        return accounts;
    }

    public void formatAccountListForSelection(int userId) {
        List<Account> checkingAccounts = accountDAO.selectCheckingAccountsByUserId(userId);
        List<Account> savingAccounts = accountDAO.selectSavingAccountsByUserId(userId);
        List<Account> allAccounts = accountDAO.selectAccountsByUserId(userId);
        if (allAccounts == null || allAccounts.isEmpty()) {
            throw new NoAccountsException("You have not created an account yet!" + "\n");
        }
        if (!checkingAccounts.isEmpty()){
            printHelper(checkingAccounts, 1,"Checking Accounts:");
            System.out.println("");
        }
        if (!savingAccounts.isEmpty()){
            printHelper(savingAccounts, checkingAccounts.size()+1, "Saving Accounts:");
            System.out.println("");
        }
    }

    private void printHelper(List<Account> accountList,int multiplier, String header){
        int count = 1 * multiplier;
        if (!accountList.isEmpty()){
            System.out.println(header);
            for (Account account : accountList) {
                System.out.println(count + " " + account.getAccountName());
                count++;
            }
        }
    }

    public List<Account> sortedAccountList(int userId){
        List<List<Account>> totalList = new ArrayList<>();
        totalList.add(accountDAO.selectCheckingAccountsByUserId(userId));
        totalList.add(accountDAO.selectSavingAccountsByUserId(userId));
        if (totalList.isEmpty()){
            throw new NoAccountsException("You have not created an account yet!");
        }
        List<Account> sortedList = new ArrayList<>();
        for (List<Account> accountList: totalList){
            for (Account account: accountList) {
                sortedList.add(account);
            }
        }
        return sortedList;
    }

    public List<Account> selectSavingAccounts(int userId){
        return accountDAO.selectSavingAccountsByUserId(userId);
    }

    public Account selectAccountById(int id){
        Account account = accountDAO.selectAccountById(id);
        if (account == null){
            throw new NullCheckingAccountException("Account does not exist!");
        }  
        return accountDAO.selectAccountById(id);
    }

    public Account selectAccountByAccountName(String accountName,int userId){
        Account account = accountDAO.selectAccountByAccountName(accountName,userId);
        if (account == null){
            throw new NullCheckingAccountException("Account does not exist");
        }
        return accountDAO.selectAccountByAccountName(accountName,userId);
    }

    public void deleteAccount(int id) {
        Account account = accountDAO.selectAccountById(id);
        if (account == null) {
            throw new NoAccountsException("Account does not exist!");
        }
        System.out.println("");
        System.out.println("Account: " + account.getAccountName() + " deleted!");
        accountDAO.deleteAccountByAccountId(id);
    }
    public void deleteAccountsByUserId(int userId){
        List<Account> accounts = accountDAO.selectAccountsByUserId(userId);
        if (accounts.isEmpty()){
            throw new NoAccountsException("There are no Accounts to delete!");
        }
        accountDAO.deleteCheckingAccountsByUserId(userId);
        System.out.println("All accounts deleted");
    }

    public Timestamp getLatestTransferDate(int accountId){
        return accountDAO.getLatestTransferDate(accountId);
    }

    public void updateAccountBalance(int accountId){
        accountDAO.updateAccountBalance(accountId);
    }
}
