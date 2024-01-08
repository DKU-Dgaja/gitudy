package com.example.backend.auth.api.service.auth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OAuthService oAuthService;

    public List<AuthLoginPageResponse> loginPage() {
        List<AuthLoginPageResponse> loginPages = oAuthService.loginPage();

        return null;
    }

    public AuthLoginResponse login(UserPlatformType platformType, String code, String state) {
        OAuthResponse loginResponse = oAuthService.login(platformType, code, state);


        return null;
    }

}
