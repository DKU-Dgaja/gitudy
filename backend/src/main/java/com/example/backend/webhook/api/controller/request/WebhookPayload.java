package com.example.backend.webhook.api.controller.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record WebhookPayload(
        String commitId,
        String message,
        String username,
        @Pattern(regexp = "^.+/.+$", message = "Repository full name must be in the format 'username/repository'")
        String repositoryFullName,
        LocalDate commitDate
) {}

