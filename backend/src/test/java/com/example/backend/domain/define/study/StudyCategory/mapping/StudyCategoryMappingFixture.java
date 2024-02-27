package com.example.backend.domain.define.study.StudyCategory.mapping;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.info.StudyInfo;

import java.util.List;
import java.util.stream.Collectors;

public class StudyCategoryMappingFixture {
    public static List<StudyCategoryMapping> generateStudyCategoryMappings(StudyInfo studyInfo, List<StudyCategory> studyCategories) {
        return studyCategories.stream()
                .map(studyCategory -> StudyCategoryMapping.builder()
                        .studyInfoId(studyInfo.getId())
                        .studyCategoryId(studyCategory.getId())
                        .build())
                .collect(Collectors.toList());
    }
}
