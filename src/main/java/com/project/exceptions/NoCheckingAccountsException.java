package com.project.exceptions;

public class NoCheckingAccountsException extends RuntimeException{
    public NoCheckingAccountsException(String message){
        super(message);
    }
}
