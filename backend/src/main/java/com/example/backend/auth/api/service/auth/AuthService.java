package com.example.backend.auth.api.service.auth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.controller.auth.response.ReissueAccessTokenResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.jwt.JwtToken;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.common.exception.state.LoginStateException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.auth.api.service.token.RefreshTokenService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.domain.define.refreshToken.RefreshToken;

import io.jsonwebtoken.Claims;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private static final String PLATFORM_ID_CLAIM = "platformId";
    private static final String PLATFORM_TYPE_CLAIM = "platformType";
    private static final String ROLE_CLAIM = "role";
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
        claims.put(PLATFORM_ID_CLAIM, user.getPlatformId());
        claims.put(PLATFORM_TYPE_CLAIM, String.valueOf(user.getPlatformType()));


        // Access Token 생성
        final String jwtAccessToken = jwtService.generateAccessToken(claims, user);

        // Refresh Token 생성
        final String jwtRefreshToken = jwtService.generateRefreshToken(claims, user);
        log.info(">>>> [ 사용자 {}님의 JWT 토큰이 발급되었습니다 ] <<<<", user.getName());
        log.info(">>>> [ 사용자 {}님의 refresh 토큰이 발급되었습니다 ] <<<<", user.getName());

        // Refresh Token을 레디스에 저장
        RefreshToken refreshToken= RefreshToken.builder().refreshToken(jwtRefreshToken).subject(user.getUsername()).build();
        refreshTokenService.saveRefreshToken(refreshToken);
        return JwtToken.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.logout(refreshToken);
    }

    // JWT 토큰이 만료되었을 때 재발급을 위한 서비스 로직
    public ReissueAccessTokenResponse reissueAccessToken(String refreshToken){
        Claims claims = jwtService.extractAllClaims(refreshToken);
        if (jwtService.isTokenValid(refreshToken, claims.getSubject())) {
            // 리프레시 토큰을 이용해 새로운 엑세스 토큰 발급
            String accessToken = refreshTokenService.reissue(claims, refreshToken);
            log.info(">>>> {} reissue AccessToken.", claims.getSubject());

            return ReissueAccessTokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } else {
            log.warn(">>>> Token Validation Fail : {}", ExceptionMessage.JWT_INVALID_RTK.getText());
            throw new JwtException(ExceptionMessage.JWT_INVALID_RTK);
        }
    }

    public UserInfoResponse getUserByInfo(String platformId, UserPlatformType platformType) {

        User userInfoResponse = userRepository.findByPlatformIdAndPlatformType(platformId, platformType)
                .orElseThrow(() -> {
                    log.warn(">>>> User not found with platformId: {} platformType: {}", platformId, platformType);
                     throw new UserException(ExceptionMessage.USER_NOT_FOUND);
                });


        return UserInfoResponse.of(userInfoResponse);

    }

}