package com.example.backend.domain.define.study.comment.study.repository;

import com.example.backend.study.api.controller.comment.study.response.StudyCommentResponse;

import java.util.List;

public interface StudyCommentRepositoryCustom {

    // 스터디 댓글 리스트 조회 쿼리 - 커서 기반 페이지네이션
    List<StudyCommentResponse> findStudyCommentListByStudyInfoIdJoinUser(Long studyInfoId, Long cursorIdx, Long limit, Long currentUserId);
}
