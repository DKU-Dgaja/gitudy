package com.example.backend.study.api.event.controller;


import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    /*
    백엔드에서 따로 메시지 확인방법이 없어서 확인하는 용도
     */
    @ApiResponse(responseCode = "200", description = "FCM Single 성공")
    @PostMapping("/single")
    public void sendMessageSingleDevice(@RequestBody FcmSingleTokenRequest token) throws FirebaseMessagingException {

        fcmService.sendMessageSingleDevice(token);
    }

    @ApiResponse(responseCode = "200", description = "FCM Multi 성공")
    @PostMapping("/multi")
    public void sendMessageMultiDevice(@RequestBody FcmMultiTokenRequest token) throws FirebaseMessagingException {

        fcmService.sendMessageMultiDevice(token);
    }
}
