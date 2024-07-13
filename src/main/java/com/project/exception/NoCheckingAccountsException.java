package com.project.exception;

public class NoCheckingAccountsException extends RuntimeException{
    public NoCheckingAccountsException(String message){
        super(message);
    }
}
