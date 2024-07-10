package com.project.exceptions;

public class NullUserUsernameException extends RuntimeException {
    public NullUserUsernameException(String message){
        super(message);
    }
}
