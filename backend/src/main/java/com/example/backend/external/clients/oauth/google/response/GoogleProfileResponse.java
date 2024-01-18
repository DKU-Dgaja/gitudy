package com.example.backend.external.clients.oauth.google.response;


import lombok.Getter;
import lombok.NoArgsConstructor;


/*
    Google에 사용자 정보를 요청 후 반환받을 DTO
 */
@Getter
@NoArgsConstructor    // 사용자 프로필 응답 데이터
public class GoogleProfileResponse {
    private String sub;  // Google에서 사용하는 사용자의 고유 식별자 (subject)
    private String name;
    private String picture;


    public GoogleProfileResponse(String sub, String name, String picture) {
        this.sub = sub;
        this.name = name;
        this.picture = picture;
    }
}
