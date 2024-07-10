package com.project.exceptions;

public class TransactionAmountBelowOneException extends RuntimeException {
    public TransactionAmountBelowOneException(String message){
        super(message);
    }
}
