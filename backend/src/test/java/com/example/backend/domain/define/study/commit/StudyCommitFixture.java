package com.example.backend.domain.define.study.commit;

import com.example.backend.study.api.service.github.response.GithubCommitResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.backend.domain.define.study.commit.constant.CommitStatus.COMMIT_APPROVAL;
import static com.example.backend.domain.define.study.commit.constant.CommitStatus.COMMIT_WAITING;

public class StudyCommitFixture {
    private static final int MAX_VALUE = 1000; // 최대값 설정

    // 테스트용 스터디 커밋 생성 메서드
    public static StudyCommit createDefaultStudyCommit(Long userId, Long studyInfoId, Long studyTodoId, String commitSHA) {
        return StudyCommit.builder()
                .studyInfoId(studyInfoId)
                .studyTodoId(studyTodoId)
                .userId(userId)
                .commitSHA(commitSHA)
                .message("메세지")
                .commitDate(LocalDate.now())
                .status(COMMIT_APPROVAL)
                .rejectionReason(null)
                .build();
    }

    public static StudyCommit createWaitingStudyCommit(Long userId, Long studyInfoId, Long studyTodoId, String commitSHA) {
        return StudyCommit.builder()
                .studyInfoId(studyInfoId)
                .studyTodoId(studyTodoId)
                .userId(userId)
                .commitSHA(commitSHA)
                .message("메세지")
                .commitDate(LocalDate.now())
                .status(COMMIT_WAITING)
                .rejectionReason(null)
                .build();
    }

    // 중복되지 않는 랜덤 값을 생성하는 메서드
    private static int generateUniqueRandomValue(Set<Integer> usedValues) {
        int randomValue;

        while (true) {
            randomValue = (int) (Math.random() * MAX_VALUE);
            if (!usedValues.contains(randomValue)) {
                usedValues.add(randomValue);
                break;
            }
        }

        return randomValue;
    }

    // 테스트용 스터디 커밋 목록 생성 메서드
    public static List<StudyCommit> createDefaultStudyCommitList(int count, Long userId, Long studyInfoId, Long studyTodoId, Set<Integer> usedValues) {
        List<StudyCommit> studyCommits = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            int randomValue = generateUniqueRandomValue(usedValues);
            usedValues.add(randomValue);

            studyCommits.add(createDefaultStudyCommit(userId, studyInfoId, studyTodoId, Integer.toString(randomValue)));
        }
        return studyCommits;
    }

    // 테스트용 깃허브 커밋 응답 객체 생성 메서드
    public static GithubCommitResponse createGithubCommitResponse(String authorName) {
        return GithubCommitResponse.builder()
                .sha("sha")
                .commitDate(LocalDate.now())
                .message("message")
                .authorName(authorName)
                .build();
    }

    // 대기 상태의 커밋 리스트 생성 메서드
    public static List<StudyCommit> createWaitingStudyCommitList(int count, Long userId, Long studyInfoId, Long studyTodoId, Set<Integer> usedValues) {
        List<StudyCommit> studyCommits = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            int randomValue = generateUniqueRandomValue(usedValues);
            usedValues.add(randomValue);

            studyCommits.add(createWaitingStudyCommit(userId, studyInfoId, studyTodoId, Integer.toString(randomValue)));
        }
        return studyCommits;
    }
}
