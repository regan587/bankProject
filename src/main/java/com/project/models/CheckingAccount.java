package com.project.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CheckingAccount {

    private int id;
    private String accountName;
    private double balance;
    private int userId;

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
                ", balance=" + String.format("%.2f", balance) +
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

    // public static double roundToTwoDecimalPlaces(double value) {
    //     return Math.round(value * 100.0) / 100.0;
    // }
    public static double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

