package com.example.backend.common.exception.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class CommitException extends GitudyException {
    public CommitException(ExceptionMessage message) {
        super(message.getText());
    }
}
