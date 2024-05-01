package com.example.backend.study.api.event.service;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.event.EventException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.fcm.repository.FcmTokenRepository;
import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.controller.request.FcmTokenSaveRequest;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmService {


    private final FirebaseMessaging firebaseMessaging;

    private final UserRepository userRepository;

    private final FcmTokenRepository fcmTokenRepository;


    public void sendMessageSingleDevice(FcmSingleTokenRequest token) throws FirebaseMessagingException {


        Notification notification = Notification.builder()
                .setTitle(token.getTitle())
                .setBody(token.getMessage())   // .setImage(token.getImage()) 이미지 사용할때
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token.getToken())
                .build();

        String response = firebaseMessaging.send(message);
        log.info(">>>> [ 메세지가 성공적으로 전송되었습니다. ] : {} <<<<", response);
    }

    public void sendMessageMultiDevice(FcmMultiTokenRequest token) throws FirebaseMessagingException {


        Notification notification = Notification.builder()
                .setTitle(token.getTitle())
                .setBody(token.getMessage())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(token.getTokens())
                .build();

        BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
        log.info(">>>> [ {}개의 메세지가 성공적으로 전송되었습니다. ] : {}", response.getSuccessCount(), response);
    }


    @Transactional
    public void saveFcmTokenRequest(User userPrincipal, FcmTokenSaveRequest token) {

        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });

        saveFcmToken(FcmToken.builder()
                .userId(user.getId())
                .fcmToken(token.getToken())
                .build());
    }

    // FCM 토큰 저장 메서드
    public void saveFcmToken(FcmToken fcmToken) {
        FcmToken savedToken = fcmTokenRepository.save(fcmToken);
        log.info(">>>> FCM Token register : {}", savedToken.getFcmToken());
    }


    public FcmToken findFcmTokenByIdOrThrowException(Long userId) {
        return fcmTokenRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.FCM_DEVICE_NOT_FOUND);
                    return new EventException(ExceptionMessage.FCM_DEVICE_NOT_FOUND);
                });
    }

    public List<FcmToken> findFcmTokensByIdsOrThrowException(List<Long> userIds) {

        // users 비어있는경우 예외처리
        if (userIds == null || userIds.isEmpty()) {
            log.warn(">>>> {} : {} <<<<", userIds, ExceptionMessage.FCM_DEVICE_NOT_FOUND);
            throw new EventException(ExceptionMessage.FCM_DEVICE_NOT_FOUND);
        }

        Iterable<FcmToken> fcmTokens = fcmTokenRepository.findAllById(userIds);

        return StreamSupport.stream(fcmTokens.spliterator(), false)
                .toList();
    }

}
