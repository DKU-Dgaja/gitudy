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
    @JsonProperty("role")
    private UserRole role;

    @NotNull
    @JsonProperty("platformId")
    private String platformId;

    @Enumerated(EnumType.STRING)
    @JsonProperty("platformType")
    private UserPlatformType platformType;

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @Email
    @JsonProperty("githubId")
    private String githubId;

}
