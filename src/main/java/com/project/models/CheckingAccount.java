package com.project.models;

public class CheckingAccount {

    private int id;
    private String accountName;
    private double totalAmount;
    private int userId;

    // Constructor, getters, and setters
    public CheckingAccount(int id, String accountName, double totalAmount, int userId) {
        this.id = id;
        this.accountName = accountName;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    public CheckingAccount(String accountName, double totalAmount, int userId) {
        this.accountName = accountName;
        this.totalAmount = totalAmount;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
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
                ", totalAmount=" + totalAmount +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckingAccount that = (CheckingAccount) o;
        return id == that.id &&
                Double.compare(that.totalAmount, totalAmount) == 0 &&
                userId == that.userId &&
                accountName.equals(that.accountName);
    }
}

