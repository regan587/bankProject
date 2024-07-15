package com.project.exception;

public class NoAccountsException extends RuntimeException {
    public NoAccountsException(String message){
        super(message);
    }
}
