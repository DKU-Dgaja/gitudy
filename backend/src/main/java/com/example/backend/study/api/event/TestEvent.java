package com.example.backend.study.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@RequiredArgsConstructor
public class TestEvent {
    private String test;
}
