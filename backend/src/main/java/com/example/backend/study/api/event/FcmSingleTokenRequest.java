package com.example.backend.study.api.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FcmSingleTokenRequest {

    // private Long userId;          Todo: userId와 token 확인을 위해 가져올것

    private String token;             // 토큰 한개를 가져올 때

    private String title;            // 메시지의 제목

    private String message;          // 메시지

    // private String image;            // 이미지
}
