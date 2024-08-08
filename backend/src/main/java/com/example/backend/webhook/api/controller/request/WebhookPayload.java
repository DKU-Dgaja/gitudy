package com.example.backend.webhook.api.controller.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record WebhookPayload(
        String commitId,
        String message,
        String username,
        @Pattern(regexp = "^.+/.+$", message = "레포지토리명은 'username/repository' 형태이여야 합니다.")
        String repositoryFullName,
        String folderName,
        LocalDate commitDate
) {}

