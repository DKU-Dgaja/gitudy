package com.example.backend.external.clients.oauth.kakao.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoProfileResponse {
    private Long id;
    private String name;

    private Properties properties;

    public KakaoProfileResponse(Long id, String name, Properties properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    @Getter
    @NoArgsConstructor
    public static class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;

        public Properties(String nickname, String profile_image, String thumbnail_image) {
            this.nickname = nickname;
            this.profile_image = profile_image;
            this.thumbnail_image = thumbnail_image;
        }
    }
}