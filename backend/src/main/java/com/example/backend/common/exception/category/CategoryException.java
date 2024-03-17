package com.example.backend.common.exception.category;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class CategoryException extends GitudyException {
    public CategoryException(ExceptionMessage message) {
        super(message.getText());
    }
}
