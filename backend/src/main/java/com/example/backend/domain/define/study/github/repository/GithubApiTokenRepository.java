package com.example.backend.domain.define.study.github.repository;

import com.example.backend.domain.define.study.github.GithubApiToken;
import org.springframework.data.repository.CrudRepository;

public interface GithubApiTokenRepository extends CrudRepository<GithubApiToken, Long> {
}
