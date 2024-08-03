package com.example.backend.auth.api.service.oauth.adapter.github;

import com.example.backend.auth.api.service.oauth.adapter.OAuthAdapter;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.oauth.OAuthException;
import com.example.backend.external.clients.oauth.github.GithubProfileClients;
import com.example.backend.external.clients.oauth.github.GithubTokenClients;
import com.example.backend.external.clients.oauth.github.response.GithubProfileResponse;
import com.example.backend.external.clients.oauth.github.response.GithubTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubAdapter implements OAuthAdapter {
    private final GithubTokenClients githubTokenClients;
    private final GithubProfileClients githubProfileClients;

    @Override
    public String getToken(String tokenURL) {
        try {
            GithubTokenResponse token = githubTokenClients.getToken(URI.create(tokenURL));

            // 받아온 token이 null일 경우 예외 발생
            if (token.getAccess_token() == null) {
                throw new OAuthException(ExceptionMessage.OAUTH_INVALID_TOKEN_URL);
            }

            return token.getAccess_token();
        } catch (RuntimeException e) {
            log.error(">>>> [ Github Oauth 인증 에러 발생: {} ] <<<<", ExceptionMessage.OAUTH_INVALID_TOKEN_URL.getText());
            throw new OAuthException(ExceptionMessage.OAUTH_INVALID_TOKEN_URL);
        }
    }

    @Override
    public OAuthResponse getProfile(String accessToken) {
        try {
            GithubProfileResponse profile = githubProfileClients.getProfile("Bearer " + accessToken);

            return OAuthResponse.builder()
                    .platformId(profile.getId().toString())
                    .platformType(GITHUB)
                    .name(profile.getLogin())
                    .profileImageUrl(profile.getAvatar_url())
                    .githubApiToken(accessToken)
                    .build();
        } catch (RuntimeException e) {
            log.error(">>>> [ Github Oauth 인증 에러 발생: {} ] <<<<", ExceptionMessage.OAUTH_INVALID_ACCESS_TOKEN.getText());
            throw new OAuthException(ExceptionMessage.OAUTH_INVALID_ACCESS_TOKEN);
        }
    }
}
