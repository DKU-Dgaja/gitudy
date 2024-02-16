package com.example.backend.common.exception.member;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class MemberException extends GitudyException {
    public MemberException(ExceptionMessage message) {
        super(message.getText());
    }
}
