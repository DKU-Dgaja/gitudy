package com.example.backend.study.api.service.github;

import com.example.backend.domain.define.study.github.GithubApiToken;
import com.example.backend.domain.define.study.github.repository.GithubApiTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GithubApiTokenService {
    private final GithubApiTokenRepository githubApiTokenRepository;

    // 사용자의 토큰 저장 로직
    @Transactional
    public GithubApiToken saveToken(String githubApiToken, Long userId) {
        // 저장하기 전 이미 토큰이 존재하는지 확인 후 삭제
        deleteToken(userId);

        return githubApiTokenRepository.save(new GithubApiToken(githubApiToken, userId));
    }

    // 사용자의 토큰 삭제 로직
    @Transactional
    public void deleteToken(Long userId) {
        githubApiTokenRepository.findByUserId(userId).ifPresent(githubApiTokenRepository::delete);
    }

}
