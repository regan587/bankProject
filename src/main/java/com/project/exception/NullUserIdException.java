package com.project.exception;

public class NullUserIdException extends RuntimeException {
    public NullUserIdException(String message){
        super(message);
    }
}
