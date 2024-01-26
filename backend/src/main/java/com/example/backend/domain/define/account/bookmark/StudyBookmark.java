package com.example.backend.domain.define.account.bookmark;

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
@Entity(name = "STUDY_BOOKMARK")
public class StudyBookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_BOOKMARK_ID")
    private Long id;                            // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_INFO_ID", nullable = false)
    private StudyInfo studyInfo;                // 즐겨찾기한 스터디 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;                          // 사용자 정보

    @Builder
    public StudyBookmark(StudyInfo studyInfo, User user) {
        this.studyInfo = studyInfo;
        this.user = user;
    }
}

