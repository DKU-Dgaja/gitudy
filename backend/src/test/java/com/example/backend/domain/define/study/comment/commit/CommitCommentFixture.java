package com.example.backend.domain.define.study.comment.commit;

import java.util.ArrayList;
import java.util.List;

public class CommitCommentFixture {
    // 테스트용 커밋 댓글 생성 메서드
    public static CommitComment createDefaultCommitComment(Long userId, Long commitId) {
        return CommitComment.builder()
                .userId(userId)
                .studyCommitId(commitId)
                .content("내용")
                .build();
    }

    // 테스트용 커밋 댓글 리스트 생성 메서드
    public static List<CommitComment> createDefaultCommitCommentList(int count, Long userId, Long commitId) {
        List<CommitComment> commitComments = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            commitComments.add(createDefaultCommitComment(userId, commitId));
        }
        return commitComments;
    }

}
