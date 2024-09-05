package com.example.backend.domain.define.study.info.repository;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.study.api.controller.info.response.StudyInfoListResponse;

import java.util.List;
import java.util.Optional;

public interface StudyInfoRepositoryCustom {

    // 정렬 기준이 있는 커서 기반 스터디 페이지네이션
    List<StudyInfoListResponse> findStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy, boolean myStudy);

    // 마이/전체 스터디 개수 조회
    int findStudyInfoCount(Long userId, boolean myStudy);

    // 레포지토리 정보를 통해 스터디 조회
    Optional<StudyInfo> findByRepositoryFullName(String owner, String repositoryName);

    // 해당 아이디가 스터디장인 스터디 전부 활동 종료
    void closeStudiesOwnedByUserId(Long userId);
}
