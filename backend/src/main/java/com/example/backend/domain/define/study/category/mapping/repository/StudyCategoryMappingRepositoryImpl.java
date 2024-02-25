package com.example.backend.domain.define.study.category.mapping.repository;

import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.category.mapping.QStudyCategoryMapping.studyCategoryMapping;

@Component
@RequiredArgsConstructor
public class StudyCategoryMappingRepositoryImpl  implements StudyCategoryMappingRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<StudyCategoryMapping> findStudyCategoryMappingListByStudyInfoIdList(List<Long> studyInfoIdList) {
        return queryFactory
                .selectFrom(studyCategoryMapping)
                .where(studyCategoryMapping.studyInfoId.in(studyInfoIdList))
                .fetch();
    }
}
