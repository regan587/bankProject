package com.project.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class Transfer {
    private int id;
    private Timestamp date;
    private double chargeAmount;
    private double remainingBalance;
    private double previousBalance;
    private int accountId;
    private int userId;

    public Transfer(int id, Timestamp date, double chargeAmount, double remainingBalance, double previousBalance, int accountId, int userId) {
        this.id = id;
        this.date = date;
        this.chargeAmount = chargeAmount;
        this.remainingBalance = remainingBalance;
        this.previousBalance = previousBalance;
        this.accountId = accountId;
        this.userId = userId;
    }

    public Transfer(double chargeAmount, int accountId, int userId, Timestamp date) {
        this.chargeAmount = chargeAmount;
        this.accountId = accountId;
        this.userId = userId;
        this.date = date;
    }

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
        return roundToTwoDecimalPlaces(chargeAmount);
    }

    public void setChargeAmount(double chargeAmount) {
        this.chargeAmount = roundToTwoDecimalPlaces(chargeAmount);
    }

    public double getRemainingBalance() {
        return roundToTwoDecimalPlaces(remainingBalance);
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = roundToTwoDecimalPlaces(remainingBalance);
    }

    public double getPreviousBalance() {
        return roundToTwoDecimalPlaces(previousBalance);
    }

    public void setPreviousBalance(double previousBalance) {
        this.previousBalance = roundToTwoDecimalPlaces(previousBalance);
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
        return "Transfer{" +
                "id=" + id +
                ", date=" + date +
                ", chargeAmount=" + String.format("%.2f", chargeAmount) +
                ", remainingBalance=" + String.format("%.2f", remainingBalance) +
                ", previousBalance=" + String.format("%.2f", previousBalance) +
                ", accountId=" + accountId +
                ", userId=" + userId +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer that = (Transfer) o;
        return id == that.id &&
                Double.compare(that.chargeAmount, chargeAmount) == 0 &&
                Double.compare(that.remainingBalance, remainingBalance) == 0 &&
                accountId == that.accountId &&
                date.equals(that.date);
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
