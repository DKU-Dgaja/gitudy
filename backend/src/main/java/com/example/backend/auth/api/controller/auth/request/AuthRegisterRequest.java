package com.example.backend.auth.api.controller.auth.request;

import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegisterRequest {
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole role;

    @NotNull
    private String platformId;

    @Enumerated(EnumType.STRING)
    private UserPlatformType platformType;

    @NotNull
    private String name;

    @NotNull
    @Email
    private String githubId;

}
