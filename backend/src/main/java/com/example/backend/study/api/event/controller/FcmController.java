package com.example.backend.study.api.event.controller;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.event.controller.request.FcmTokenSaveRequest;
import com.example.backend.study.api.event.service.FcmService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;


























    @ApiResponse(responseCode = "200", description = "FCM token 저장 성공")
    @PostMapping("")
    public void saveFcmToken(@AuthenticationPrincipal User userPrincipal,
                             @RequestBody FcmTokenSaveRequest token) {
        fcmService.saveFcmTokenRequest(userPrincipal, token);
    }
}