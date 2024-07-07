package com.example.backend.domain.define.study.comment.commit.repository;

import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;

import java.util.List;

public interface CommitCommentRepositoryCustom {
    // CommitId로 커밋에 달린 댓글과 유저 정보를 조인해서 전부 가져오기
    List<CommitCommentInfoResponse> findCommitCommentListByCommitIdJoinUser(Long commitId);
}
