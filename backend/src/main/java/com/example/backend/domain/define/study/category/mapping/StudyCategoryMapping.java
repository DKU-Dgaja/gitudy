package com.example.backend.domain.define.study.category.mapping;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.info.StudyInfo;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_INFO_ID", nullable = false)
    private StudyInfo studyInfo;                // 스터디 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_CATEGORY_ID", nullable = false)
    private StudyCategory category;             // 스터디 카테고리 정보

    @Builder
    public StudyCategoryMapping(StudyInfo studyInfo, StudyCategory category) {
        this.studyInfo = studyInfo;
        this.category = category;
    }

}
