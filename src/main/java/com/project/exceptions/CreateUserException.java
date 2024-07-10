package com.project.exceptions;

public class CreateUserException extends RuntimeException{
    public CreateUserException(String message){
        super(message);
    }
}
