package com.example.backend.common.exception.auth;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class AuthException extends GitudyException {
    public AuthException(ExceptionMessage message) {
        super(message.getText());
    }
}
