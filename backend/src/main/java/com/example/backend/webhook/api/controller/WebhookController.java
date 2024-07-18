package com.example.backend.webhook.api.controller;

import com.example.backend.webhook.api.controller.request.WebhookPayload;
import com.example.backend.webhook.api.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/commit")
    public ResponseEntity<Void> handleCommitWebhook(@RequestBody @Valid WebhookPayload payload) {

        webhookService.handleCommit(payload);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
