package com.example.backend.domain.define.fcm.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.study.api.event.service.FcmService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FcmTokenRepositoryTest extends TestConfig {
    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

    @AfterEach
    void tearDown() {
        fcmTokenRepository.deleteAll();
        userRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("FcmToken을 저장할 수 있다.")
    void redisLoginStateSave() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        FcmToken fcmToken = fcmTokenRepository.save(FcmToken.builder()
                .userId(user.getId())
                .fcmToken("fcmToken")
                .build());

        // when
        FcmToken savedFcmToken = fcmTokenRepository.findById(fcmToken.getUserId()).get();

        // then
        assertThat(fcmToken.getUserId()).isEqualTo(savedFcmToken.getUserId());
        assertThat(fcmToken.getFcmToken()).isEqualTo(savedFcmToken.getFcmToken());
    }

/*    @Test
    void FcmToken_저장_확인() throws FirebaseMessagingException {
        FcmToken token = FcmToken.builder()
                .userId(1L)
                .fcmToken("c3utrmlFQiKr_xEULJ4S53:APA91bFitFx_tpLzoRVtjBchTGGDkpJvt2LQPa-9cbWdEDReeJUFDrQNzWLkainciK7aWTKJ0Ppx48nKbfMsWsVWCSWIdD_lWxG83h_7TOaYXYZ4jxVt6y_ucqTC-2A4F1PFAAlhY746")
                .build();
        fcmTokenRepository.save(token);


        FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(1L);

        fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                .token(fcmToken.getFcmToken())
                .title("[" +   "] 커밋 승인")
                .message("TO-DO [ ]에 대한 커밋이 승인되었습니다.\n팀장의 커밋 리뷰를 확인해보세요!")
                .build());
    }

    @Test
    void FcmTokens_저장_확인() throws FirebaseMessagingException {


        FcmToken token = FcmToken.builder()
                .userId(1L)
                .fcmToken("3")
                .build();
        fcmTokenRepository.save(token);

        FcmToken token2 = FcmToken.builder()
                .userId(2L)
                .fcmToken("test")
                .build();
        fcmTokenRepository.save(token2);

        FcmToken token3 = FcmToken.builder()
                .userId(3L)
                .fcmToken("1")
                .build();
        fcmTokenRepository.save(token3);

        // userId 리스트 생성
        List<Long> userIds = List.of(token.getUserId(), token2.getUserId(), token3.getUserId());



        List<FcmToken> fcmTokens = fcmService.findFcmTokensByIdsOrThrowException(userIds);

        // 토큰 문자열 리스트 반환
        List<String> tokens = fcmTokens.stream()
                .map(FcmToken::getFcmToken)
                .toList();

        fcmService.sendMessageMultiDevice(FcmMultiTokenRequest.builder()
                .tokens(tokens)
                .title("[" +   "] 커밋 승인")
                .message("TO-DO [ ]에 대한 커밋이 승인되었습니다.\n팀장의 커밋 리뷰를 확인해보세요!")
                .build());
    }*/
}