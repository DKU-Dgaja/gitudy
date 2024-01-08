package com.example.backend.auth.api.service.oauth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.service.oauth.adapter.OAuthAdapter;
import com.example.backend.auth.api.service.oauth.adapter.github.OAuthGithubAdapter;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.backend.domain.define.user.constant.UserPlatformType.GITHUB;

@Slf4j
@Service
public class OAuthService {

    public List<AuthLoginPageResponse> loginPage() {

        return null;
    }

    public OAuthResponse login(UserPlatformType platformType, String code, String state) {

        return null;
    }
}
