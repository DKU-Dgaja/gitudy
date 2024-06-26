package com.example.backend.study.api.event.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmTokenSaveRequest {

    private String token;             // 토큰 한개를 가져올 때
}