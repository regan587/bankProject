package com.project.exceptions;

public class NullUserIdException extends RuntimeException {
    public NullUserIdException(String message){
        super(message);
    }
}
