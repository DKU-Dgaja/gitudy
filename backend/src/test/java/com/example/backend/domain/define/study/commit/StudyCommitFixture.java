package com.example.backend.domain.define.study.commit;

import com.example.backend.domain.define.study.commit.constant.CommitStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.backend.domain.define.study.commit.constant.CommitStatus.COMMIT_APPROVAL;

public class StudyCommitFixture {

    // 테스트용 스터디 커밋 생성 메서드
    public static StudyCommit createDefaultStudyCommit(Long userId, Long studyInfoId, String commitSHA) {
        return StudyCommit.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .commitSHA(commitSHA)
                .message("메세지")
                .commitDate(LocalDate.now())
                .status(COMMIT_APPROVAL)
                .rejectionReason(null)
                .build();
    }

    // 테스트용 스터디 커밋 목록 생성 메서드
    public static List<StudyCommit> createDefaultStudyCommitList(int count, Long userId, Long studyInfoId) {
        List<StudyCommit> studyCommits = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyCommits.add(createDefaultStudyCommit(userId, studyInfoId, String.valueOf(i)));
        }
        return studyCommits;
    }
}
