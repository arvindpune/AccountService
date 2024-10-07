package com.dws.challenge.exception;

public class AccountIdNotFound extends RuntimeException{

    public AccountIdNotFound(String message){
        super(message);
    }
}
