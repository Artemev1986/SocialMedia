package com.example.socialmedia.exception;

public class CurrencyNotValidException extends RuntimeException {
    public CurrencyNotValidException(String message) {
        super(message);
    }
}