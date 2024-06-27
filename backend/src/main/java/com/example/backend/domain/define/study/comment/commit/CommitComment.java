package com.example.backend.domain.define.study.comment.commit;

import com.example.backend.domain.define.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "COMMIT_COMMENT")
public class CommitComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMIT_COMMENT_ID")
    private Long id;                            // 아이디

    @Column(name = "STUDY_COMMIT_ID", nullable = false)
    private Long studyCommitId;                 // 커밋 ID

    @Column(name = "USER_ID", nullable = false)
    private Long userId;                        // 사용자 ID

    @Column(name = "CONTENT", nullable = false)
    private String content;                     // 댓글 내용

    @Builder
    public CommitComment(Long studyCommitId, Long userId, String content) {
        this.studyCommitId = studyCommitId;
        this.userId = userId;
        this.content = content;
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
