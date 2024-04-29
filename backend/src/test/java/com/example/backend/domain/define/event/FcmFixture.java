package com.example.backend.domain.define.event;

import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.FcmSingleTokenRequest;

import java.util.List;

public class FcmFixture {

    public static FcmSingleTokenRequest generateFcmSingleTokenRequest() {
        return FcmSingleTokenRequest.builder()
                .token("token")
                .title("title")
                .message("message")
                .build();
    }

    public static FcmMultiTokenRequest generateFcmMultiTokenRequest() {
        return FcmMultiTokenRequest.builder()
                .tokens(List.of("token1", "token2", "token3"))
                .title("title")
                .message("message")
                .build();
    }

    public static FcmToken generateDefaultFcmToken(Long userId) {
        return FcmToken.builder()
                .userId(userId)
                .fcmToken("token")
                .build();
    }

    public static List<FcmToken> generateFcmTokens(List<Long> userIds) {
        return userIds.stream()
                .map(FcmFixture::generateDefaultFcmToken)
                .toList();
    }

}
