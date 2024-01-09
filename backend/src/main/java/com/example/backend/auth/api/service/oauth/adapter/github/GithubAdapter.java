package com.example.backend.auth.api.service.oauth.adapter.github;

import com.example.backend.auth.api.service.oauth.adapter.OAuthAdapter;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import org.springframework.stereotype.Component;

@Component
public class GithubAdapter implements OAuthAdapter {

    @Override
    public String getToken(String tokenURL) {
        return null;
    }

    @Override
    public OAuthResponse getProfile(String accessToken) {
        return null;
    }
}
