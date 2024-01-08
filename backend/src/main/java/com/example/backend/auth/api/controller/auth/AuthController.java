package com.example.backend.auth.api.controller.auth;

import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    @GetMapping("/loginPage")
    public JsonResult<AuthLoginPageResponse> loginPage() {

        return JsonResult.successOf();
    }

    @GetMapping("/{platformType}/login")
    public JsonResult<AuthLoginResponse> login(
            @PathVariable("platformType") UserPlatformType platformType,
            @RequestParam("code") String code,
            @RequestParam("state") String loginState) {

        return JsonResult.successOf();
    }
}
