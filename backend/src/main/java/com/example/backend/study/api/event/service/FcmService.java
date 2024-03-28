package com.example.backend.study.api.event.service;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.fcmToken.FcmToken;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.study.api.event.controller.request.FcmTokenSaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmService {



    private final UserRepository userRepository;

    private final FcmTokenRepository fcmTokenRepository;








































    @Transactional
    public void saveFcmTokenRequest(User userPrincipal, FcmTokenSaveRequest token) {

        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });

        saveRefreshToken(FcmToken.builder()
                .userId(user.getId())
                .fcmToken(token.getToken())
                .build());
    }

    // FCM 토큰 저장 메서드
    public void saveRefreshToken(FcmToken fcmToken) {
        FcmToken savedToken = fcmTokenRepository.save(fcmToken);
        log.info(">>>> FCM Token register : {}", savedToken.getFcmToken());
    }
}
