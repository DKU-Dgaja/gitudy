package com.example.backend.domain.define.study.commit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.backend.domain.define.study.commit.constant.CommitStatus.COMMIT_APPROVAL;

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
}
