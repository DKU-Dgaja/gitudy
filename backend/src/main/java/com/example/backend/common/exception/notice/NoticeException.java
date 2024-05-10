package com.example.backend.common.exception.notice;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class NoticeException extends GitudyException {

    public NoticeException(ExceptionMessage message) {
        super(message.getText());
    }
}
