package com.example.backend.common.utils;

import com.example.backend.domain.define.account.user.User;

import java.util.HashMap;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.expectedUserPlatformId;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;

public class TokenUtil {
    public static Map<String, String> createTokenMap(User user) {
        Map<String, String> map = new HashMap<>();
        map.put("role", String.valueOf(USER));
        map.put("platformId", expectedUserPlatformId);
        map.put("platformType", String.valueOf(GITHUB));
        return map;
    }
}
