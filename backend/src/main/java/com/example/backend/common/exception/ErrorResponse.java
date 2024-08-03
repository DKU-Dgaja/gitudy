package com.example.backend.common.exception;

public record ErrorResponse (
        int status,
        String title,
        String message
) {
    public static ErrorResponse from(int status, String title, String message) {
        return new ErrorResponse(status, title, message);
    }
}
