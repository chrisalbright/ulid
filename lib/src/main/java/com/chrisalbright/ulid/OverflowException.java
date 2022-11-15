package com.chrisalbright.ulid;

public class OverflowException extends RuntimeException {
    public OverflowException(String message) {
        super(message);
    }
}
