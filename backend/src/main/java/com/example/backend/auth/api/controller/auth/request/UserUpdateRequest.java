package com.example.backend.auth.api.controller.auth.request;

import com.example.backend.common.validation.ValidSocialInfo;
import com.example.backend.domain.define.account.user.SocialInfo;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotEmpty
    private String name;                                        // 이름

    @NotEmpty
    private String profileImageUrl;                             // 프로필 사진

    private boolean profilePublicYn;                            // 프로필 공개 여부

    @Nullable
    @ValidSocialInfo
    private SocialInfo socialInfo;                              // 소셜 정보

}
