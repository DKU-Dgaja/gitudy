package com.example.backend.auth.api.service.oauth;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.oauth.adapter.github.GithubAdapter;
import com.example.backend.auth.api.service.oauth.adapter.kakao.KakaoAdapter;
import com.example.backend.auth.api.service.oauth.builder.github.GithubURLBuilder;
import com.example.backend.auth.api.service.oauth.builder.kakao.KakaoURLBuilder;
import com.example.backend.auth.api.service.oauth.adapter.google.GoogleAdapter;
import com.example.backend.auth.api.service.oauth.builder.google.GoogleURLBuilder;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.common.exception.oauth.OAuthException;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.example.backend.domain.define.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.user.constant.UserPlatformType.KAKAO;
import static com.example.backend.domain.define.user.constant.UserPlatformType.GOOGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OAuthServiceTest extends TestConfig {
    @Autowired
    private GithubURLBuilder urlBuilder;

    @Autowired
    private OAuthService oAuthService;

    @MockBean
    private GithubAdapter githubAdapter;

    @Autowired

    private KakaoURLBuilder kakaoUrlBuilder;
    @MockBean
    private KakaoAdapter kakaoAdapter;

    @Autowired
    private GoogleURLBuilder googleurlBuilder;

    @MockBean
    private GoogleAdapter googleAdapter;

    @Test
    @DisplayName("모든 플랫폼의 로그인 페이지를 성공적으로 반환한다.")
    void allUrlBuilderSuccess() {
        // given
        String state = "test state";

        // when
        List<AuthLoginPageResponse> loginPages = oAuthService.loginPage(state);
        String authorizeURL = urlBuilder.authorize(state);

        String authorizeURLkakao = kakaoUrlBuilder.authorize(state);
        // then

        String authorizeURLgoogle = googleurlBuilder.authorize(state);

        // then
        //assertThat(loginPages.get(0).getUrl()).isEqualTo(authorizeURL);

        assertThat(loginPages).hasSize(3); // 리스트 크기 확인

        // 각 플랫폼별 URL인지 확인
        boolean containsGithub = loginPages.stream()
                .anyMatch(page -> page.getPlatformType().equals(GITHUB) &&
                        page.getUrl().equals(authorizeURL));
        boolean containsKakao = loginPages.stream()
                .anyMatch(page -> page.getPlatformType().equals(KAKAO) &&
                        page.getUrl().equals(authorizeURLkakao));

        assertThat(containsGithub).isTrue();
        assertThat(containsKakao).isTrue();
        boolean containsGoogle = loginPages.stream()
                .anyMatch(page -> page.getPlatformType().equals(GOOGLE) &&
                        page.getUrl().equals(authorizeURLgoogle));

        assertThat(containsGithub).isTrue();
        assertThat(containsGoogle).isTrue();
    }

    @Test
    @DisplayName("깃허브 로그인에 성공하면 OAuthResponse 객체를 반환한다.")
    void githubLoginSuccess() {
        // given
        String code = "valid-code";
        String state = "valid-state";

        OAuthResponse response = generateOauthResponse();

        // when
        // when 사용시 Mockito 패키지 사용
        when(githubAdapter.getToken(any(String.class))).thenReturn("access-token");
        when(githubAdapter.getProfile(any(String.class))).thenReturn(response);
        OAuthResponse profile = oAuthService.login(GITHUB, code, state);

        // then
        assertThat(profile)
                .extracting("platformId", "platformType", "email", "name", "profileImageUrl")
                .contains("1", GITHUB, "32183520@dankook.ac.kr", "jusung-c", "http://www.naver.com");


    }

    @Test
    @DisplayName("카카오 로그인에 성공하면 OAuthResponse 객체를 반환한다.")
    void kakaoLoginSuccess() {
        // given
        String code = "valid-code";
        String state = "valid-state";

        OAuthResponse response = OAuthResponse.builder()
                .platformId("1")
                .platformType(KAKAO)
                .email("이메일 없음")
                .name("구영민")
                .profileImageUrl("http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg")
                .build();

        // when
        // when 사용시 Mockito 패키지 사용
        when(kakaoAdapter.getToken(any(String.class))).thenReturn("access-token");
        when(kakaoAdapter.getProfile(any(String.class))).thenReturn(response);
        OAuthResponse profile = oAuthService.login(KAKAO, code, state);

        // then
        assertThat(profile)
                .extracting("platformId", "platformType", "email", "name", "profileImageUrl")
                .contains("1", KAKAO, "이메일 없음", "구영민", "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg");


    }
    @Test
    @DisplayName("깃허브 로그인에 실패하면 OAuthException 예외가 발생한다.")
    void githubLoginFail() {
        // given
        String code = "invalid-code";
        String state = "invalid-state";

        // when
        when(githubAdapter.getToken(any(String.class))).thenThrow(OAuthException.class);
        assertThrows(OAuthException.class,
                () -> oAuthService.login(GITHUB, code, state));
    }

    @Test
    @DisplayName("구글 로그인에 성공하면 OAuthResponse 객체를 반환한다.")
    void googleLoginSuccess() {
        // given
        String code = "valid-code";
        String state = "valid-state";

        OAuthResponse response = OAuthResponse.builder()
                .platformId("102514823309503386675")
                .platformType(GOOGLE)
                .email("xw21yog@dankook.ac.kr")
                .name("이정우")
                .profileImageUrl("https://lh3.googleusercontent.com/a/ACg8ocLrP_GLo-fUjSmnUZedPZbbL7ifImYTnelh108XkgOx=s96-c")
                .build();

        // when
        // when 사용시 Mockito 패키지 사용
        when(googleAdapter.getToken(any(String.class))).thenReturn("access-token");
        when(googleAdapter.getProfile(any(String.class))).thenReturn(response);
        OAuthResponse profile = oAuthService.login(GOOGLE, code, state);

        // then
        assertThat(profile)
                .extracting("platformId", "platformType", "email", "name", "profileImageUrl")
                .contains("102514823309503386675", GOOGLE, "xw21yog@dankook.ac.kr", "이정우", "https://lh3.googleusercontent.com/a/ACg8ocLrP_GLo-fUjSmnUZedPZbbL7ifImYTnelh108XkgOx=s96-c");


    }

    @Test
    @DisplayName("구글 로그인에 실하면 OAuthException 예외가 발생한다.")
    void googleLoginFail() {
        // given
        String code = "invalid-code";
        String state = "invalid-state";

        // when
        when(googleAdapter.getToken(any(String.class))).thenThrow(OAuthException.class);
        assertThrows(OAuthException.class,
                () -> oAuthService.login(GOOGLE, code, state));

    }

    @Test
    @DisplayName("카카오 로그인에 실패하면 OAuthException 예외가 발생한다.")
    void kakaoLoginFail() {
        // given
        String code = "invalid-code";
        String state = "invalid-state";

        // when
        when(kakaoAdapter.getToken(any(String.class))).thenThrow(OAuthException.class);
        assertThrows(OAuthException.class,
                () -> oAuthService.login(KAKAO, code, state));

    }
}