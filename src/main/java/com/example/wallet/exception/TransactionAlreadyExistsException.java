package com.example.wallet.exception;

public class TransactionAlreadyExistsException extends RuntimeException {

    public TransactionAlreadyExistsException() {
        super("Transaction.already.exists");
    }
}