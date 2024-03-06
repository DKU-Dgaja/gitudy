package com.example.backend.study.api.service.info.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNameAndProfileImageResponse {
    private Long id;

    private String name;                        // 이름

    private String profileImageUrl;             // 사용자 ID
}
