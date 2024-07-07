package com.example.backend.domain.define.notice.repository;

import com.example.backend.domain.define.notice.Notice;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepositoryCustom {

    // UserId와 현재시간으로 유저의 알림 목록을 조회한다.
    List<Notice> findUserNoticeListByUserId(Long userId, LocalDateTime time, Long limit);
}
