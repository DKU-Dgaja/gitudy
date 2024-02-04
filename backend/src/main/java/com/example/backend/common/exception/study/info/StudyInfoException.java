package com.example.backend.common.exception.study.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class StudyInfoException extends GitudyException {
    public StudyInfoException(ExceptionMessage message) {
        super(message.getText());
    }
}
