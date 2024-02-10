package com.example.backend.domain.define.study.info.repository;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.study.api.controller.info.response.AllStudyInfoResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyInfoRepositoryCustom {
    // 오프셋 기반 마이 스터디 페이지네이션
    Page<StudyInfo> findStudyInfoListByUserId_OffsetPaging(Pageable pageable, Long userId);

    // 커서 기반 마이 스터디 페이지네이션
    List<StudyInfoResponse> findStudyInfoListByUserId_CursorPaging(Long userId, Long idx, Long limit);

    // 정렬 기준이 있는 커서 기반 마이 스터디 페이지네이션
    List<AllStudyInfoResponse> findStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy);
}
