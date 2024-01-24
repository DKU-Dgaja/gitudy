package com.example.backend.auth.api.service.auth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.jwt.JwtToken;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.auth.api.service.token.RefreshTokenService;
import com.example.backend.domain.define.refreshToken.RefreshToken;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.constant.UserRole;
import com.example.backend.domain.define.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private static final String ROLE_CLAIM = "role";
    private static final String NAME_CLAIM = "name";
    private static final String PROFILE_IMAGE_CLAIM = "profileImageUrl";

    private final UserRepository userRepository;
    private final OAuthService oAuthService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    @Transactional
    public AuthLoginResponse login(UserPlatformType platformType, String code, String state) {
        OAuthResponse loginResponse = oAuthService.login(platformType, code, state);
        String name = loginResponse.getName();
        String profileImageUrl = loginResponse.getProfileImageUrl();
        String platformId = loginResponse.getPlatformId();

        log.info(">>>> [ {}님이 로그인하셨습니다 ] <<<<", name);

        /*
         * OAuth 로그인 인증을 마쳤으니 우리 애플리케이션의 DB에도 존재하는 사용자인지 확인한다.
         * 회원이 아닐 경우, 즉 회원가입이 필요한 신규 사용자의 경우 OAuthResponse를 바탕으로 DB에 등록해준다.
         */
        User findUser = userRepository.findByPlatformIdAndPlatformType(platformId, platformType)
                .orElseGet(() -> {
                    User saveUser = User.builder()
                            .platformId(platformId)
                            .platformType(loginResponse.getPlatformType())
                            .role(UserRole.UNAUTH)
                            .name(name)
                            .profileImageUrl(profileImageUrl)
                            .build();

                    log.info(">>>> [ UNAUTH 권한으로 사용자를 DB에 등록합니다. 이후 회원가입이 필요합니다 ] <<<<");
                    return userRepository.save(saveUser);
                });

        // 기존 사용자의 경우 OAuth 사용자 정보(이름, 사진)가 변경되었으면 업데이트해준다.
        findUser.updateProfile(name, profileImageUrl);

        /*
            DB에 저장된 사용자 정보를 기반으로 JWT 토큰을 발급
            * JWT 토큰을 요청시에 담아 보내면 JWT 토큰 인증 필터에서 Security Context에 인증된 사용자로 등록
            TODO : JWT 재발급을 위한 Refresh 토큰은 Redis에서 관리할 예정입니다.
         */
        JwtToken jwtToken = generateJwtToken(findUser);

        // JWT 토큰과 권한 정보를 담아 반환
        return AuthLoginResponse.builder()
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .role(findUser.getRole())
                .build();
    }

    private JwtToken generateJwtToken(User user) {
        // JWT 토큰 생성을 위한 claims 생성
        HashMap<String, String> claims = new HashMap<>();
        claims.put(ROLE_CLAIM, user.getRole().name());
        claims.put(NAME_CLAIM, user.getName());
        claims.put(PROFILE_IMAGE_CLAIM, user.getProfileImageUrl());

        // Access Token 생성
        final String jwtAccessToken = jwtService.generateAccessToken(claims, user);

        // Refresh Token 생성
        final String jwtRefreshToken = jwtService.generateRefreshToken(claims, user);
        log.info(">>>> [ 사용자 {}님의 JWT 토큰이 발급되었습니다 ] <<<<", user.getName());
        log.info(">>>> [ 사용자 {}님의 refresh 토큰이 발급되었습니다 ] <<<<", user.getName());

        // Refresh Token을 레디스에 저장
        RefreshToken refreshToken= RefreshToken.builder().refreshToken(jwtRefreshToken).platformId(user.getPlatformId()).build();
        refreshTokenService.saveRefreshToken(refreshToken);
        return JwtToken.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    /*
        TODO : 로그아웃을 처리하는 서비스 로직 필요합니다.
     */

    /*
        TODO : JWT 토큰이 만료되었을 때 재발급을 위한 서비스 로직 필요합니다.
     */


}
