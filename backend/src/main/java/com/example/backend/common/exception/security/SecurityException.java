package com.example.backend.common.exception.security;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class SecurityException extends GitudyException {
    public SecurityException(ExceptionMessage message) {
        super(message.getText());
    }
}
