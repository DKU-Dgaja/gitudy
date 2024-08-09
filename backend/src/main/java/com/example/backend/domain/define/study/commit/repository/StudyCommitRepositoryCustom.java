package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;

import java.util.List;

public interface StudyCommitRepositoryCustom {

    // 커서 기반 마이 커밋 페이지네이션
    List<CommitInfoResponse> findStudyCommitListByUserId_CursorPaging(Long userId, Long studyId, Long cursorIdx, Long limit);

    // GithubCommitResponse 리스트 중 실제 StudyCommit으로 저장되지 않은 경우 찾기
    List<GithubCommitResponse> findUnsavedGithubCommits(List<GithubCommitResponse> githubCommitList);

}
