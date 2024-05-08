package com.example.backend.study.api.event.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class UserNoticeList {

    private String id;

    private Long studyInfoId;

    private String title;

    private String message;

    private LocalDateTime localDateTime;
}
