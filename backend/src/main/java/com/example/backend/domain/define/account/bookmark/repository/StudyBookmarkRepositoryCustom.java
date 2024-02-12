package com.example.backend.domain.define.account.bookmark.repository;

import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;

import java.util.List;

public interface StudyBookmarkRepositoryCustom {
    // 북마크 스터디 목록 조회 쿼리 - 커서 기반 페이지네이션
    List<BookmarkInfoResponse> findStudyBookmarkListByUserIdJoinStudyInfo(Long userId, Long cursorIdx, Long limit);
}
