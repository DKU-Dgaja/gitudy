package com.example.backend.common.exception.state;

import com.example.backend.common.exception.ExceptionMessage;

public class LoginStateException extends RuntimeException {
    public LoginStateException(ExceptionMessage message) {
        super(message.getText());
    }
}
