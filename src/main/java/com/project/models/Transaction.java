package com.project.models;

import java.sql.Timestamp;

public class Transaction {
    private int id;
    private Timestamp date;
    private String description;
    private double chargeAmount;
    private double totalAmountRemaining;
    private double previousAmount;
    private int accountId;
    private int userId;

    // Constructor, getters, and setters
    public Transaction(int id, Timestamp date, String description, double chargeAmount, double totalAmountRemaining, double previousAmount, int accountId, int userId) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.chargeAmount = chargeAmount;
        this.totalAmountRemaining = totalAmountRemaining;
        this.previousAmount = previousAmount;
        this.accountId = accountId;
        this.userId = userId;
    }

    // public Transaction(String description, double chargeAmount, double totalAmountRemaining, double previousAmount, int accountId, int userId, Timestamp date) {
    //     this.description = description;
    //     this.chargeAmount = chargeAmount;
    //     this.totalAmountRemaining = totalAmountRemaining;
    //     this.previousAmount = previousAmount;
    //     this.accountId = accountId;
    //     this.userId = userId;
    //     this.date = date; // Assigning timestamp parameter
    // }

    // public Transaction(String description, double chargeAmount, double totalAmountRemaining, double previousAmount, int accountId, int userId) {
    //     this.description = description;
    //     this.chargeAmount = chargeAmount;
    //     this.totalAmountRemaining = totalAmountRemaining;
    //     this.previousAmount = previousAmount;
    //     this.accountId = accountId;
    //     this.userId = userId;
    // }

    public Transaction(String description, double chargeAmount, int accountId, int userId, Timestamp date) {
        this.description = description;
        this.chargeAmount = chargeAmount;
        this.accountId = accountId;
        this.userId = userId;
        this.date = date;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public double getTotalAmountRemaining() {
        return totalAmountRemaining;
    }

    public void setTotalAmountRemaining(double totalAmountRemaining) {
        this.totalAmountRemaining = totalAmountRemaining;
    }

    public double getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(double previousAmount) {
        this.previousAmount = previousAmount;
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
                ", description='" + description + '\'' +
                ", chargeAmount=" + chargeAmount +
                ", totalAmountRemaining=" + totalAmountRemaining +
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
                Double.compare(that.totalAmountRemaining, totalAmountRemaining) == 0 &&
                accountId == that.accountId &&
                date.equals(that.date) &&
                description.equals(that.description);
    }
}
