package com.example.backend.common.exception.user;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class UserException extends GitudyException {
    public UserException(ExceptionMessage message) {
        super(message.getText());
    }
}
