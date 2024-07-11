package com.project.models;

public class CheckingAccount {

    private int id;
    private String accountName;
    private double balance;
    private int userId;

    // Constructor, getters, and setters
    public CheckingAccount(int id, String accountName, double balance, int userId) {
        this.id = id;
        this.accountName = accountName;
        this.balance = balance;
        this.userId = userId;
    }

    public CheckingAccount(String accountName, double balance, int userId) {
        this.accountName = accountName;
        this.balance = balance;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getbalance() {
        return balance;
    }

    public void setbalance(double balance) {
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CheckingAccount{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckingAccount that = (CheckingAccount) o;
        return id == that.id &&
                Double.compare(that.balance, balance) == 0 &&
                userId == that.userId &&
                accountName.equals(that.accountName);
    }
}

