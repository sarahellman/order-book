package com.example.orderbook.api.exceptionhandling;

public class SuspiciousDeviationException extends RuntimeException {
    public SuspiciousDeviationException(String message) {
        super(message);
    }
}