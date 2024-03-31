package com.example.backend.domain.define.study.info.listener;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcmToken.FcmToken;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.listener.event.ApplyMemberEvent;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.event.FcmTitleMessageRequest;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.any;

@SuppressWarnings("NonAsciiCharacters")
public class StudyEventListenerTest extends TestConfig {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyMemberService studyMemberService;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @MockBean
    private StudyEventListener studyEventListener;


    @AfterEach()
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        fcmTokenRepository.deleteAll();
    }


    @Test
    @DisplayName("스터디 가입신청 리스너 테스트")
    void apply_notify_test() throws FirebaseMessagingException {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUserPushAlarmY();  // 알람여부 true 추가
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(leader.getId());
        fcmTokenRepository.save(fcmToken);

        FcmTitleMessageRequest request = FcmFixture.generateFcmTitleMessageRequest();

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);

        // then
        Mockito.verify(studyEventListener).applyMemberListener(any(ApplyMemberEvent.class)); // applyMemberListener 호출 검증

    }
}
