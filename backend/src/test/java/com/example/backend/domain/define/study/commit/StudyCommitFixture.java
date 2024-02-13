package com.example.backend.domain.define.study.commit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.backend.domain.define.study.commit.constant.CommitStatus.COMMIT_APPROVAL;

public class StudyCommitFixture {
    public static final Long expectedStudyId = 1L;
    public static final Long expectedUserId = 1L;
    public static final String expectedMessage = "커밋 메세지";

    // 테스트용 스터디 커밋 생성 메서드
    public static StudyCommit createDefaultStudyCommit(String commitSHA) {
        return StudyCommit.builder()
                .studyInfoId(expectedStudyId)
                .userId(expectedUserId)
                .commitSHA(commitSHA)
                .message(expectedMessage)
                .commitDate(LocalDate.now())
                .status(COMMIT_APPROVAL)
                .rejectionReason(null)
                .build();
    }

    // 테스트용 스터디 커밋 목록 생성 메서드
    public static List<StudyCommit> createDefaultStudyCommitList(int count) {
        List<StudyCommit> studyCommits = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyCommits.add(createDefaultStudyCommit(String.valueOf(i)));
        }
        return studyCommits;
    }
}
