package com.example.backend.common.exception.event;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class EventException extends GitudyException {
    public EventException(ExceptionMessage message) {
        super(message.getText());
    }
}
