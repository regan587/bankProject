package com.project.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavingAccount {

    private int id;
    private String accountName;
    private double balance;
    private double interestRate;
    private int userId;

    public SavingAccount(int id, String accountName, double balance, double interestRate, int userId) {
        this.id = id;
        this.accountName = accountName;
        this.balance = balance;
        this.interestRate = interestRate;
        this.userId = userId;
    }

    public SavingAccount(String accountName, double balance, double interestRate, int userId) {
        this.accountName = accountName;
        this.balance = balance;
        this.interestRate = interestRate;
        this.userId = userId;
    }

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

    public double getBalance() {
        return roundToTwoDecimalPlaces(balance);
    }

    public void setBalance(double balance) {
        this.balance = roundToTwoDecimalPlaces(balance);
    }

    public double getInterestRate() {
        return roundToTwoDecimalPlaces(interestRate);
    }

    public void setInterestRate(double balance) {
        this.balance = roundToTwoDecimalPlaces(interestRate);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SavingAccount{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", balance=" + String.format("%.2f", balance) +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavingAccount that = (SavingAccount) o;
        return id == that.id &&
                Double.compare(that.balance, balance) == 0 &&
                userId == that.userId &&
                accountName.equals(that.accountName);
    }

    public static double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
}
