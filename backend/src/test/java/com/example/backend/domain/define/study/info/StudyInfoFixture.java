package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;

import java.time.LocalDate;
public class StudyInfoFixture {

    // 테스트용 스터디 정보 생성 메서드
    public static StudyInfo createDefaultStudyInfo(Long userId) {
        return StudyInfo.builder()
                .userId(userId)
                .topic("Sample Study")
                .score(10)
                .endDate(LocalDate.now().plusMonths(3))
                .info("info")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode("ABC123")
                .maximumMember(5)
                .currentMember(3)
                .lastCommitDay(LocalDate.now())
                .profileImageUrl("https://example.com/profile.jpg")
                .notice("Notice")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();
    }
}