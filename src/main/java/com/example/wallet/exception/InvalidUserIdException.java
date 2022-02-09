package com.example.wallet.exception;

public class InvalidUserIdException extends RuntimeException {

    public InvalidUserIdException() {
        super("Invalid.user_id");
    }
}