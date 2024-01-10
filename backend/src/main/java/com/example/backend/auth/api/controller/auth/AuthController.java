package com.example.backend.auth.api.controller.auth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    private final OAuthService oAuthService;

    @GetMapping("/loginPage")
    public JsonResult<List<AuthLoginPageResponse>> loginPage() {

        // 일단 임시로 state값을 넣어줬습니다.
        List<AuthLoginPageResponse> loginPages = oAuthService.loginPage("randomState");

        return JsonResult.successOf(loginPages);
    }

    @GetMapping("/{platformType}/login")
    public JsonResult<String> login(
            @PathVariable("platformType") UserPlatformType platformType,
            @RequestParam("code") String code,
            @RequestParam("state") String loginState) {

        authService.login(platformType, code, loginState);

        return JsonResult.successOf("로그인에 성공하였습니다.");
    }
}
