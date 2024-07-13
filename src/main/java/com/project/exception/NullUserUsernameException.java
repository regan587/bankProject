package com.project.exception;

public class NullUserUsernameException extends RuntimeException {
    public NullUserUsernameException(String message){
        super(message);
    }
}
