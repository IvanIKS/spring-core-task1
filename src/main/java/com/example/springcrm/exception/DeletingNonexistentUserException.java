package com.example.springcrm.exception;

public class DeletingNonexistentUserException extends Exception {
    public DeletingNonexistentUserException(String message) {
        super(message);
    }
}
