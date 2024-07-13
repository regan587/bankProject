package com.project.exception;

public class DuplicateCheckingAccountNameException extends RuntimeException {
    public DuplicateCheckingAccountNameException(String message){
        super(message);
    }
}
