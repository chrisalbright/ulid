package com.chrisalbright.ulid;

public class UnderflowException extends RuntimeException{
    public UnderflowException(String message) {
        super(message);
    }
}
