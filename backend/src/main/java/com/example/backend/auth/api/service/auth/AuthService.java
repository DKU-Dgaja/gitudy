package com.example.backend.auth.api.service.auth;

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

    public AuthLoginResponse login(UserPlatformType platformType, String code, String state) {
        OAuthResponse loginResponse = oAuthService.login(platformType, code, state);
        log.info(">>>> {}님이 로그인하셨습니다.", loginResponse.getName());

        /*
            TODO : OAuth 로그인 인증을 마쳤으니 우리 애플리케이션의 DB에도 존재하는 사용자인지 확인해야 합니다.
                * 회원이 아닐 경우, 즉 회원가입이 필요한 신규 사용자의 경우 OAuthResponse를 바탕으로 DB에 등록해줍니다.
         */

        /*
            TODO : 기존 사용자의 경우 OAuth 사용자 정보가 변경되었을 수 있으므로 변경 사항을 업데이트해주는 로직이 필요합니다.
         */

        /*
            TODO : DB에 저장된 사용자 정보를 기반으로 JWT 토큰을 발급해주는 로직이 필요합니다.
                * JWT 토큰을 요청시에 담아 인증된 사용자임을 알립니다.
                * JWT 토큰 인증 필터에서 Security에 인증된 사용자로 등록될 것입니다.
                * JWT 재발급을 위한 Refresh 토큰은 Redis에서 관리할 예정입니다.
         */

        // TODO : JWT 토큰을 담아 최종적으로 AuthLoginResponse를 반환해줍니다.
        return null;
    }

    /*
        TODO : 로그아웃을 처리하는 서비스 로직 필요합니다.
     */

    /*
        TODO : JWT 토큰이 만료되었을 때 재발급을 위한 서비스 로직 필요합니다.
     */

}
