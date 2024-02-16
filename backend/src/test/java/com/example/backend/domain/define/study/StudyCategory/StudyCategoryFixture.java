package com.example.backend.domain.define.study.StudyCategory;

import com.example.backend.domain.define.study.category.info.StudyCategory;

public class StudyCategoryFixture {
    public static StudyCategory createDefaultPublicStudyCategory(String name) {
        return StudyCategory.builder()
                .name(name)
                .build();
    }
}
