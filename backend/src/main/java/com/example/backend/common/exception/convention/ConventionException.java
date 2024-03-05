package com.example.backend.common.exception.convention;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class ConventionException extends GitudyException {
    public ConventionException(ExceptionMessage message) {
        super(message.getText());
    }
}
