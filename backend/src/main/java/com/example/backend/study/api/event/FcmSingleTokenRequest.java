package com.example.backend.study.api.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmSingleTokenRequest {

    private String token;             // 토큰 한개를 가져올 때

    private String title;            // 메시지의 제목

    private String message;          // 메시지


    public static FcmSingleTokenRequest of(String token, String title, String message) {
        return FcmSingleTokenRequest.builder()
                .token(token)
                .title(title)
                .message(message)
                .build();
    }
}
