package com.project.models;

import java.sql.Timestamp;

public class Transaction {
    private int id;
    private Timestamp date;
    private double chargeAmount;
    private double remainingBalance;
    private double previousBalance;
    private int accountId;
    private int userId;

    // Constructor, getters, and setters
    public Transaction(int id, Timestamp date, double chargeAmount, double remainingBalance, double previousBalance, int accountId, int userId) {
        this.id = id;
        this.date = date;
        this.chargeAmount = chargeAmount;
        this.remainingBalance = remainingBalance;
        this.previousBalance = previousBalance;
        this.accountId = accountId;
        this.userId = userId;
    }

    // public Transaction(String , double chargeAmount, double remainingBalance, double previousBalance, int accountId, int userId, Timestamp date) {
    //     this. = ;
    //     this.chargeAmount = chargeAmount;
    //     this.remainingBalance = remainingBalance;
    //     this.previousBalance = previousBalance;
    //     this.accountId = accountId;
    //     this.userId = userId;
    //     this.date = date; // Assigning timestamp parameter
    // }

    // public Transaction(String , double chargeAmount, double remainingBalance, double previousBalance, int accountId, int userId) {
    //     this. = ;
    //     this.chargeAmount = chargeAmount;
    //     this.remainingBalance = remainingBalance;
    //     this.previousBalance = previousBalance;
    //     this.accountId = accountId;
    //     this.userId = userId;
    // }

    public Transaction(double chargeAmount, int accountId, int userId, Timestamp date) {
        this.chargeAmount = chargeAmount;
        this.accountId = accountId;
        this.userId = userId;
        this.date = date;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public double getremainingBalance() {
        return remainingBalance;
    }

    public void setremainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public double getpreviousBalance() {
        return previousBalance;
    }

    public void setpreviousBalance(double previousBalance) {
        this.previousBalance = previousBalance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", date=" + date +
                ", chargeAmount=" + chargeAmount +
                ", remainingBalance=" + remainingBalance +
                ", accountId=" + accountId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id &&
                Double.compare(that.chargeAmount, chargeAmount) == 0 &&
                Double.compare(that.remainingBalance, remainingBalance) == 0 &&
                accountId == that.accountId &&
                date.equals(that.date);
    }
}
