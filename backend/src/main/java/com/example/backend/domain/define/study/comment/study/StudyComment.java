package com.example.backend.domain.define.study.comment.study;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.info.StudyInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_COMMENT")
public class StudyComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_COMMENT_ID")
    private Long id;                            // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_INFO_ID", nullable = false)
    private StudyInfo studyInfo;                // 속한 스터디 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;                          // 작성자 정보

    @Column(name = "CONTENT", nullable = false)
    private String content;                     // 댓글 내용

    @Builder
    public StudyComment(StudyInfo studyInfo, User user, String content) {
        this.studyInfo = studyInfo;
        this.user = user;
        this.content = content;
    }
}
