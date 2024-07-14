package com.project.exception;

public class SavingAccountBelowZeroException extends RuntimeException {
    public SavingAccountBelowZeroException(String message){
        super(message);
    }
}
