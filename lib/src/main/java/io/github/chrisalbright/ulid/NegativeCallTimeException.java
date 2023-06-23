package io.github.chrisalbright.ulid;

public class NegativeCallTimeException extends RuntimeException {
    public NegativeCallTimeException(String msg) {
        super(msg);
    }
}
