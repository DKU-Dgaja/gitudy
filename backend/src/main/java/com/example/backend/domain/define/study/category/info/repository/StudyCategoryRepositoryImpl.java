package com.example.backend.domain.define.study.category.info.repository;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.category.info.QStudyCategory.studyCategory;

@Component
@RequiredArgsConstructor
public class StudyCategoryRepositoryImpl implements StudyCategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyCategory> findStudyCategoryListByCategoryIdList(List<Long> categoryIdList){
        return queryFactory
                .selectFrom(studyCategory)
                .where(studyCategory.id.in(categoryIdList))
                .fetch();
    }
}
