package io.github.chrisalbright.ulid;

public class UnderflowException extends RuntimeException{
    public UnderflowException(String message) {
        super(message);
    }
}
