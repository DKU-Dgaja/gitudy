package com.example.backend.common.exception.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class GithubApiTokenException extends GitudyException {
    public GithubApiTokenException(ExceptionMessage message) {
        super(message.getText());
    }
}
