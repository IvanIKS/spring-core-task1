package com.example.springcrm.exception;

public class OutdatedUsernameException extends RuntimeException {
    public OutdatedUsernameException(String message) {
        super(message);
    }
}
