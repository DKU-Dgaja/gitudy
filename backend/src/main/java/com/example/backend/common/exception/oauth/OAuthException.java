package com.example.backend.common.exception.oauth;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class OAuthException extends GitudyException {

    public OAuthException(ExceptionMessage message) {
        super(message.getText());
    }
}
