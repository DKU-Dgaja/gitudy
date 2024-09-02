package com.example.backend.study.api.service.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiTokenException;
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

    @Transactional(readOnly = true)
    public GithubApiToken getToken(Long userId) {
        return githubApiTokenRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<" , userId, ExceptionMessage.GITHUB_API_TOKEN_NOT_EXIST.getText());
            throw new GithubApiTokenException(ExceptionMessage.GITHUB_API_TOKEN_NOT_EXIST);
        });
    }

    // 사용자의 토큰 저장 로직
    @Transactional
    public GithubApiToken saveToken(String githubApiToken, Long userId) {
        // 저장하기 전 이미 토큰이 존재하는지 확인 후 삭제
        log.info("토큰을 저장하기 전에 기존 토큰의 삭제를 시도합니다. (userId: {})", userId);
        deleteToken(userId);
        log.info("기존 토큰의 삭제를 완료하였습니다. 이제 토큰 저장을 시도합니다. (userId: {})", userId);

        GithubApiToken savedToken = githubApiTokenRepository.save(new GithubApiToken(githubApiToken, userId));
        log.info("사용자의 토큰을 저장했습니다. (userId: {})", userId);

        return savedToken;
    }

    // 사용자의 토큰 삭제 로직
    @Transactional
    public void deleteToken(Long userId) {
        githubApiTokenRepository.findByUserId(userId).ifPresent(githubApiTokenRepository::delete);
    }

}
