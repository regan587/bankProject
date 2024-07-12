package com.project.exceptions;

public class TransferAmountBelowOneException extends RuntimeException {
    public TransferAmountBelowOneException(String message){
        super(message);
    }
}
