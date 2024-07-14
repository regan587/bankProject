package com.project.exception;

public class DuplicateSavingAccountNameException extends RuntimeException {
    public DuplicateSavingAccountNameException(String message){
        super(message);
    }
}
