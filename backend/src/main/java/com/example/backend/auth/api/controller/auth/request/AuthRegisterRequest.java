package com.example.backend.auth.api.controller.auth.request;

import jakarta.validation.constraints.Email;
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
    @NotNull
    private String name;

    @NotNull
    @Email
    private String githubId;

}
