package com.example.backend.domain.define.test.repository;

import com.example.backend.domain.define.test.DslTest;

import java.util.Optional;

public interface DslTestRepositoryCustom {
    // UserId로 DslTest를 조회한다. Join Fetch로 User 정보까지 가져온다.
    Optional<DslTest> findDslTestByUserId(Long userId);
}
