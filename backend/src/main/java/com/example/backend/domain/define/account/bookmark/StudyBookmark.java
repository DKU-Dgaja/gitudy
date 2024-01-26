package com.example.backend.domain.define.account.bookmark;

import com.example.backend.domain.define.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_BOOKMARK")
public class StudyBookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_BOOKMARK_ID")
    private Long id;                            // 아이디

    @Column(name = "STUDY_INFO_ID", nullable = false)
    private Long studyInfoId;                   // 즐겨찾기한 스터디 ID

    @Column(name = "USER_ID", nullable = false)
    private Long userId;                        // 사용자 ID

    @Builder
    public StudyBookmark(Long studyInfoId, Long userId) {
        this.studyInfoId = studyInfoId;
        this.userId = userId;
    }
}

