package com.example.backend.webhook.api.controller.request;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record WebhookPayload(
        String commitId,
        String message,
        String username,
        @Pattern(regexp = "^.+/.+$", message = "Repository full name must be in the format 'username/repository'")
        String repositoryFullName,
        LocalDate commitDate
) {}

