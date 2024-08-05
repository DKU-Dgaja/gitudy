package com.example.backend.auth.api.service.oauth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.service.oauth.adapter.OAuthAdapter;
import com.example.backend.auth.api.service.oauth.adapter.github.GithubAdapter;
import com.example.backend.auth.api.service.oauth.adapter.google.GoogleAdapter;
import com.example.backend.auth.api.service.oauth.adapter.kakao.KakaoAdapter;
import com.example.backend.auth.api.service.oauth.builder.OAuthURLBuilder;
import com.example.backend.auth.api.service.oauth.builder.github.GithubURLBuilder;
import com.example.backend.auth.api.service.oauth.builder.google.GoogleURLBuilder;
import com.example.backend.auth.api.service.oauth.builder.kakao.KakaoURLBuilder;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.*;

@Slf4j
@Service
public class OAuthService {
    // 각 플랫폼에 해당하는 Factory 객체를 매핑해 관리한다.
    private Map<UserPlatformType, OAuthFactory> adapterMap;

    // 플랫폼별 Adapter, URLBuilder 등록


    public OAuthService(GithubAdapter githubAdapter, GithubURLBuilder githubURLBuilder, GoogleAdapter googleAdapter, GoogleURLBuilder googleURLBuilder, KakaoAdapter kakaoAdapter, KakaoURLBuilder kakaoURLBuilder) {
        this.adapterMap = new HashMap<>() {{
            // 깃허브 플랫폼 추가
            put(GITHUB, OAuthFactory.builder()
                    .oAuthAdapter(githubAdapter)
                    .oAuthURLBuilder(githubURLBuilder)
                    .build());

            // 카카오 플랫폼 추가
            put(KAKAO, OAuthFactory.builder()
                    .oAuthAdapter(kakaoAdapter)
                    .oAuthURLBuilder(kakaoURLBuilder)
                    .build());

            // 구글 플랫폼 추가
            put(GOOGLE, OAuthFactory.builder()
                    .oAuthAdapter(googleAdapter)
                    .oAuthURLBuilder(googleURLBuilder)
                    .build());

        }};
    }

    // OAuth 2.0 로그인 페이지 생성
    public List<AuthLoginPageResponse> loginPage(String state) {
        // 지원하는 모든 플랫폼의 로그인 페이지를 생성해 반환한다.
        List<AuthLoginPageResponse> urls = adapterMap.keySet().stream()
                .map(type -> {
                    // 각 플랫폼에 해당하는 OAuthFactory 획득
                    OAuthFactory oAuthFactory = adapterMap.get(type);

                    // URL 빌더를 사용해 로그인 페이지 URL 생성
                    String loginPage = oAuthFactory.getOAuthURLBuilder().authorize(state);

                    // 로그인 페이지 DTO 생성
                    return AuthLoginPageResponse.builder()
                            .platformType(type)
                            .url(loginPage)
                            .build();
                })
                .collect(Collectors.toList());

        return urls;
    }

    // OAuth 2.0 로그인 요청 메서드
    public OAuthResponse login(UserPlatformType platformType, String code, String state) {
        OAuthFactory factory = adapterMap.get(platformType);

        OAuthURLBuilder urlBuilder = factory.getOAuthURLBuilder();
        OAuthAdapter adapter = factory.getOAuthAdapter();
        log.info(">>>> [ {} Login Start ] <<<<", platformType);

        // code, state를 이용해 Access Token 요청 URL 생성
        String tokenUrl = urlBuilder.token(code, state);

        // Access Token 획득
        String accessToken = adapter.getToken(tokenUrl);

        // 사용자 프로필 조회
        OAuthResponse userInfo = adapter.getProfile(accessToken);
        log.info(">>>> [ {} Login Success ] <<<<", platformType);

        return userInfo;
    }
}
