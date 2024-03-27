package com.example.backend.common.exception.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;

public class GithubApiException extends GitudyException {
    public GithubApiException(ExceptionMessage message) {
        super(message.getText());
    }
}
