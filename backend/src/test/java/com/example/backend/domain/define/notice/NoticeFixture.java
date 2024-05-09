package com.example.backend.domain.define.notice;

import java.time.LocalDateTime;

public class NoticeFixture {

    public static Notice generateDefaultNotice(String id, Long userId, String title, LocalDateTime localDateTime) {
        return Notice.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .message("메세지")
                .localDateTime(localDateTime)
                .build();
    }
}
