package com.example.backend.domain.define.study.convention;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.study.info.StudyInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_CONVENTION")
public class StudyConvention extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_CONVENTION_ID")
    private Long id;                            // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_INFO_ID", nullable = false)
    private StudyInfo studyInfo;                // 속한 스터디 정보

    @Column(name = "NAME", nullable = false)
    private String name;                        // 컨벤션 이름

    @Column(name = "DESCRIPTION")
    private String description;                 // 컨벤션 설명

    @Column(name = "CONTENT", nullable = false)
    private String content;                     // 컨벤션 내용 (정규식)

    @Column(name = "IS_ACTIVE")
    private boolean isActive = true;           // 컨벤션 적용 여부

    @Builder
    public StudyConvention(StudyInfo studyInfo, String name, String description, String content, boolean isActive) {
        this.studyInfo = studyInfo;
        this.name = name;
        this.description = description;
        this.content = content;
        this.isActive = isActive;
    }
}
