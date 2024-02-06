package com.example.backend.common.exception.todo;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class TodoException extends GitudyException {
    public TodoException(ExceptionMessage message) {
        super(message.getText());
    }
}
