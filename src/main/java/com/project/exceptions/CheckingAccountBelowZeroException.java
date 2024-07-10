package com.project.exceptions;

public class CheckingAccountBelowZeroException extends RuntimeException{
    public CheckingAccountBelowZeroException(String message){
        super(message);
    }
}
