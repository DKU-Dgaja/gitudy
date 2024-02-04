package com.example.backend.domain.define.test.repository;

import com.example.backend.domain.define.test.DslTest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.test.QDslTest.dslTest;

@Component
@RequiredArgsConstructor
public class DslTestRepositoryImpl implements DslTestRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<DslTest> findDslTestByUserId(Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(dslTest)
                .innerJoin(user)
                .on(dslTest.userId.eq(user.id))
                .where(user.id.eq(userId))
                .fetchOne());
    }
}
