package com.example.backend.common.exception.jwt;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class JwtException extends GitudyException {
    public JwtException(ExceptionMessage message) {
        super(message.getText());
    }
}
