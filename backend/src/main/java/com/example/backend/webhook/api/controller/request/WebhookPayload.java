package com.example.backend.webhook.api.controller.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
public record WebhookPayload(
        @Pattern(regexp = "^.+/.+$", message = "레포지토리명은 'username/repository' 형태이여야 합니다.")
        String repositoryFullName,
        List<CommitPayload> commits
) {}
