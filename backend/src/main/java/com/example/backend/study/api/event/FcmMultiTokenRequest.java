package com.example.backend.study.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmMultiTokenRequest {

    private List<String> tokens;     // 토큰을 여러개 가져올 때

    private String title;            // 메시지의 제목

    private String message;          // 메시지

}
