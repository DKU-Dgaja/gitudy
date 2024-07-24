package com.example.backend.domain.define.study.github.repository;

import com.example.backend.domain.define.study.github.GithubApiToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GithubApiTokenRepository extends CrudRepository<GithubApiToken, String> {

    // 사용자 아이디로 토큰 조회
    Optional<GithubApiToken> findByUserId(Long userId);

    // 사용자의 저장된 토큰이 있는지 확인
    boolean existsByUserId(Long userId);

}
