package com.example.backend.study.api.event.service;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.notice.Notice;
import com.example.backend.domain.define.notice.NoticeFixture;
import com.example.backend.domain.define.notice.repository.NoticeRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.event.controller.response.UserNoticeList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
class NoticeServiceTest extends MockTestConfig {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @AfterEach
    void tearDown() {
        noticeRepository.deleteAll();
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("알림 목록 조회 테스트")
    public void readNoticeList() {
        // given

        LocalDateTime time = LocalDateTime.now();
        Long limit = 3L;

        User user = UserFixture.generateAuthUser();
        User user2 = UserFixture.generateKaKaoUser();
        userRepository.saveAll(List.of(user, user2));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(user.getId());
        studyInfoRepository.save(studyInfo);

        UserInfoResponse userInfo = authService.findUserInfo(user);
        UserInfoResponse userInfo2 = authService.findUserInfo(user2);

        Notice notice1 = NoticeFixture.generateDefaultNotice("string1", userInfo.getUserId(), "2일전알림", time.minusDays(2));
        Notice notice2 = NoticeFixture.generateDefaultNotice("string2", userInfo.getUserId(), "두시간전알림", time.minusHours(2));
        Notice notice3 = NoticeFixture.generateDefaultNotice("string3", userInfo.getUserId(), "3일전알림", time.minusDays(3));
        Notice notice4 = NoticeFixture.generateDefaultNotice("string4", userInfo2.getUserId(), "한시간전알림", time.minusHours(1));
        noticeRepository.saveAll(List.of(notice1, notice2, notice3, notice4));

        // when
        List<UserNoticeList> userNoticeList = noticeService.ReadNoticeList(userInfo, time, limit);

        // then
        assertEquals(3, userNoticeList.size()); // user의 알림목록 3개 확인
        assertEquals("두시간전알림", userNoticeList.get(0).getTitle());
        assertEquals("2일전알림", userNoticeList.get(1).getTitle());
        assertEquals("3일전알림", userNoticeList.get(2).getTitle());
    }
}
