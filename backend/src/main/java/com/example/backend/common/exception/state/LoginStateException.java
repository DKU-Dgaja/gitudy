package com.example.backend.common.exception.state;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class LoginStateException extends GitudyException {
    public LoginStateException(ExceptionMessage message) {
        super(message.getText());
    }
}
