package com.example.backend.study.api.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmTitleMessageRequest {

    private String title;            // 메시지의 제목

    private String message;          // 메시지
}
