package com.example.backend.common.exception.webhook;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class WebhookException extends GitudyException {
    public WebhookException(ExceptionMessage message) {
        super(message.getText());
    }
}
