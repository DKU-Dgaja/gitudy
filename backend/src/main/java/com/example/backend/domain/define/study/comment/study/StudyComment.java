package com.example.backend.domain.define.study.comment.study;

import com.example.backend.domain.define.BaseEntity;
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

    @Column(name = "STUDY_INFO_ID", nullable = false)
    private Long studyInfoId;                   // 스터디 ID

    @Column(name = "USER_ID", nullable = false)
    private Long userId;                        // 사용자 ID

    @Column(name = "CONTENT", nullable = false)
    private String content;                     // 댓글 내용

    @Builder
    public StudyComment(Long studyInfoId, Long userId, String content) {
        this.studyInfoId = studyInfoId;
        this.userId = userId;
        this.content = content;
    }

    public void updateStudyComment(String content) {
        this.content = content;
    }
}
