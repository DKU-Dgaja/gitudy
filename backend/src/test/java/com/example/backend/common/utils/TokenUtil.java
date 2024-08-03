package com.example.backend.common.utils;

import com.example.backend.domain.define.account.user.User;

import java.util.HashMap;
import java.util.Map;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;

public class TokenUtil {
    public static Map<String, String> createTokenMap(User user) {
        Map<String, String> map = new HashMap<>();
        map.put("role", String.valueOf(user.getRole()));
        map.put("platformId", user.getPlatformId());
        map.put("platformType", String.valueOf(GITHUB));
        return map;
    }
}
