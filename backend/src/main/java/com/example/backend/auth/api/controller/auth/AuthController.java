package com.example.backend.auth.api.controller.auth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
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
        /*
            TODO : state 값을 생성해 Redis에 저장해두는 로직이 필요합니다.
                * 이 state 값은 CSRF 보호를 위해 사용됩니다.
         */

        // 일단 임시로 state값을 넣어줬습니다.
        List<AuthLoginPageResponse> loginPages = oAuthService.loginPage("randomState");

        return JsonResult.successOf(loginPages);
    }

    @GetMapping("/{platformType}/login")
    public JsonResult<AuthLoginResponse> login(
            @PathVariable("platformType") UserPlatformType platformType,
            @RequestParam("code") String code,
            @RequestParam("state") String loginState) {
        /*
            TODO : state 값이 유효한지 검증하는 로직이 필요합니다.
         */

        AuthLoginResponse loginResponse = authService.login(platformType, code, loginState);

        return JsonResult.successOf(loginResponse);
    }

    /*
        TODO : 로그아웃을 처리하는 컨트롤러가 필요합니다.
     */

    /*
        TODO : JWT 토큰이 만료되었을 때 재발급을 처리할 컨트롤러가 필요합니다.
     */
}
