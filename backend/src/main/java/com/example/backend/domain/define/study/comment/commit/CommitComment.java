package com.example.backend.domain.define.study.comment.commit;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.info.StudyInfo;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_COMMIT_ID", nullable = false)
    private StudyCommit studyCommit;            // 커밋 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;                          // 작성자 정보

    @Column(name = "CONTENT", nullable = false)
    private String content;                     // 댓글 내용

    @Builder
    public CommitComment(StudyCommit studyCommit, User user, String content) {
        this.studyCommit = studyCommit;
        this.user = user;
        this.content = content;
    }
}
