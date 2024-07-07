package com.example.backend.domain.define.account.user.repository;

import com.example.backend.domain.define.account.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.user.QUser.user;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findUserListByStudyMemberIdList(List<Long> studyMemberIdList) {
        return queryFactory
                .selectFrom(user)
                .where(user.id.in(studyMemberIdList))
                .fetch();
    }
}
