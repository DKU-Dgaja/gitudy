package com.example.backend.domain.define.study.category.info;

import com.example.backend.domain.define.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_CATEGORY")
public class StudyCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_CATEGORY_ID")
    private Long id;            // 아이디

    @Column(name = "NAME", nullable = false)
    private String name;        // 카테고리 이름

    @Builder
    public StudyCategory(String name) {
        this.name = name;
    }
}
