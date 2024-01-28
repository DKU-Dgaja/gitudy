package com.example.backend.domain.define.study.category.mapping;

import com.example.backend.domain.define.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_CATEGORY_MAPPING")
public class StudyCategoryMapping extends BaseEntity {

    @Id
    @Column(name = "STUDY_CATEGORY_MAPPING_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                            // 아이디

    @Column(name = "STUDY_INFO_ID", nullable = false)
    private Long studyInfoId;                   // 스터디 ID

    @Column(name = "STUDY_CATEGORY_ID", nullable = false)
    private Long studyCategoryId;               // 카테고리 ID

    @Builder
    public StudyCategoryMapping(Long studyInfoId, Long studyCategoryId) {
        this.studyInfoId = studyInfoId;
        this.studyCategoryId = studyCategoryId;
    }
}
