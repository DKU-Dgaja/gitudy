package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.controller.info.response.StudyInfoResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.temporal.ChronoUnit;
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

    // 테스트용 스터디 정보 생성 메서드 (Score와 LastCommitDay는 랜덤 값으로 생성)
    public static StudyInfo createDefaultStudyInfoRandomScoreAndLastCommitDay(Long userId) {
        Random random = new Random();
        int minScore = 1;
        int maxScore = 100;
        int randomScore = random.nextInt(maxScore - minScore + 1) + minScore;

        // 마지막 커밋 날짜를 현재 날짜를 기준으로 랜덤으로 설정
        LocalDate now = LocalDate.now();
        long daysToAdd = random.nextInt(365);
        LocalDate randomLastCommitDay = now.minusDays(daysToAdd);

        return StudyInfo.builder()
                .userId(userId)
                .topic("Sample Study")
                .score(randomScore) // 랜덤 스코어 설정
                .endDate(now.plusMonths(3))
                .info("info")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode("ABC123")
                .maximumMember(5)
                .currentMember(3)
                .lastCommitDay(randomLastCommitDay) // 랜덤 마지막 커밋 날짜 설정
                .profileImageUrl("https://example.com/profile.jpg")
                .notice("Notice")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();
    }


    // 테스트용 스터디 정보 목록 생성 메서드
    public static List<StudyInfo> createDefaultStudyInfoList(int count, Long userId) {
        List<StudyInfo> studyInfos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyInfos.add(createDefaultStudyInfo(userId));
        }
        return studyInfos;
    }

    // 테스트용 스터디 정보 생성 메서드 (Score와 LastCommitDay는 랜덤 값으로 생성)
    public static List<StudyInfo> createDefaultStudyInfoListRandomScoreAndLastCommitDay(int count, Long userId) {
        List<StudyInfo> studyInfos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyInfos.add(createDefaultStudyInfoRandomScoreAndLastCommitDay(userId));
        }
        return studyInfos;
    }
}
