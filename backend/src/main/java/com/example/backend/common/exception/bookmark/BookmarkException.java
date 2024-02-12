package com.example.backend.common.exception.bookmark;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class BookmarkException extends GitudyException {
    public BookmarkException(ExceptionMessage message) {
        super(message.getText());
    }

}
