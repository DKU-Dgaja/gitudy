package com.example.backend.common.exception;

public abstract class GitudyException extends RuntimeException {
    public GitudyException(String message) {
        super(message);
    }
}
