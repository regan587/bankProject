package com.project.exception;

public class CheckingAccountBelowZeroException extends RuntimeException{
    public CheckingAccountBelowZeroException(String message){
        super(message); 
    }
}
