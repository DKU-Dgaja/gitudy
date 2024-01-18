package com.example.backend.auth;

/*
* static 모음 *
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

* Mocking *
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 */


import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.constant.UserRole;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.example.backend.domain.define.user.constant.UserPlatformType.GITHUB;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TestConfig {

    public static User generateUser() {

        return User.builder()
                .platformId("1234")
                .platformType(UserPlatformType.KAKAO)
                .role(UserRole.USER)
                .name("홍길동")
                .phoneNumber("010-0000-0000")
                .profileImageUrl("https://google.com")
                .pushAlarmYn(true)
                .build();
    }

    public static OAuthResponse generateOauthResponse() {
        return OAuthResponse.builder()
                .platformId("1")
                .platformType(GITHUB)
                .name("jusung-c")
                .profileImageUrl("http://www.naver.com")
                .build();
    }
}

