package com.example.backend.common.exception.comment.study;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class StudyCommentException extends GitudyException {
    public StudyCommentException(ExceptionMessage message) {
        super(message.getText());
    }
}