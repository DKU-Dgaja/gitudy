package com.example.backend.common.exception.member;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class StudyMemberException extends GitudyException {

    public StudyMemberException(ExceptionMessage message) {
        super(message.getText());
    }
}
