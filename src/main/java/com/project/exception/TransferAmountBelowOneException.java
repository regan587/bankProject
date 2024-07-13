package com.project.exception;

public class TransferAmountBelowOneException extends RuntimeException {
    public TransferAmountBelowOneException(String message){
        super(message);
    }
}
