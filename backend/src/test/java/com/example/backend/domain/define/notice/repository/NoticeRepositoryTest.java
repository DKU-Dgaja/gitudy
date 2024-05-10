package com.example.backend.domain.define.notice.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.notice.Notice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class NoticeRepositoryTest extends TestConfig {
    @Autowired
    NoticeRepository noticeRepository;

    @AfterEach
    void tearDown() {
        noticeRepository.deleteAll();
    }

    @Test
    @DisplayName("알람을 저장하고 userId로 검색할 수 있다")
    void saveAndFindNoticeByUserIdTest() {
        Long userId = 1L;
        String title = "title";
        String message = "message";
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        // given
        Notice notice = Notice.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .localDateTime(localDateTime)
                .build();

        // when
        noticeRepository.save(notice);
        List<Notice> foundNotice = noticeRepository.findByUserId(userId);

        // then
        assertThat(foundNotice).isNotNull();
        assertThat(foundNotice.get(0).getUserId()).isEqualTo(userId);
        assertThat(foundNotice.get(0).getTitle()).isEqualTo(title);
        assertThat(foundNotice.get(0).getMessage()).isEqualTo(message);
        assertThat(foundNotice.get(0).getLocalDateTime()).isEqualTo(localDateTime);
    }
}