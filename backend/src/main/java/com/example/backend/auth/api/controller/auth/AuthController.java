package com.example.backend.auth.api.controller.auth;

import com.example.backend.auth.api.controller.auth.request.AuthRegisterRequest;
import com.example.backend.auth.api.controller.auth.response.AuthLoginPageResponse;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.controller.auth.response.ReissueAccessTokenResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.response.AuthServiceLoginResponse;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.state.LoginStateService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;
import com.example.backend.common.exception.oauth.OAuthException;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
import java.util.List;

import static com.example.backend.domain.define.account.user.constant.UserRole.UNAUTH;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final static int REFRESH_TOKEN_INDEX = 2;
    private final AuthService authService;
    private final OAuthService oAuthService;
    private final LoginStateService loginStateService;


    @GetMapping("/loginPage")
    public JsonResult<List<AuthLoginPageResponse>> loginPage() {

        String loginState = loginStateService.generateLoginState();

        // OAuth 사용하여 각 플랫폼의 로그인 페이지 URL을 가져와서 state 주입
        List<AuthLoginPageResponse> loginPages = oAuthService.loginPage(loginState);


        return JsonResult.successOf(loginPages);
    }

    @GetMapping("/{platformType}/login")
    public JsonResult<AuthLoginResponse> login(
            @PathVariable("platformType") UserPlatformType platformType,
            @RequestParam("code") String code,
            @RequestParam("state") String loginState) {

        // state 값이 유효한지 검증
        if (!loginStateService.isValidLoginState(loginState)) {
            throw new OAuthException(ExceptionMessage.LOGINSTATE_INVALID_VALUE);
        }

        AuthLoginResponse loginResponse = authService.login(platformType, code, loginState);

        return JsonResult.successOf(loginResponse);
    }

    @GetMapping("/logout")
    public JsonResult<?> logout(@RequestHeader(name = "Authorization") String token) {
        List<String> tokens = Arrays.asList(token.split(" "));


        if (tokens.size() == 3) {
            authService.logout(tokens.get(REFRESH_TOKEN_INDEX));

            return JsonResult.successOf("로그아웃 되었습니다.");
        } else {
            log.warn(">>>> Invalid Header Access : {}", ExceptionMessage.JWT_INVALID_HEADER.getText());
            return JsonResult.failOf(ExceptionMessage.JWT_INVALID_HEADER.getText());
        }

    }

    // JWT 토큰이 만료되었을 때 재발급을 처리할 컨트롤러
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = ReissueAccessTokenResponse.class)))
    @PostMapping("/reissue")
    public JsonResult<?> reissueAccessToken(@RequestHeader(name = "Authorization") String token) {
        List<String> tokens = Arrays.asList(token.split(" "));
        if (tokens.size() == 3) {
            ReissueAccessTokenResponse reissueResponse = authService.reissueAccessToken(tokens.get(REFRESH_TOKEN_INDEX));

            return JsonResult.successOf(reissueResponse);
        } else {
            log.warn(">>>> Invalid Header Access : {}", ExceptionMessage.JWT_INVALID_HEADER.getText());
            return JsonResult.failOf(ExceptionMessage.JWT_INVALID_HEADER.getText());
        }
    }

    @GetMapping("/info")
    public JsonResult<UserInfoResponse> userInfo(@AuthenticationPrincipal User user) {

        if (user.getRole() == UNAUTH) {
            log.error(">>>> {} <<<<", ExceptionMessage.UNAUTHORIZED_AUTHORITY);
            return JsonResult.failOf(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText());
        }

        UserInfoResponse userInfoResponse = authService.getUserByInfo(user.getPlatformId(), user.getPlatformType());

        return JsonResult.successOf(userInfoResponse);
    }

    @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = AuthServiceLoginResponse.class)))
    @PostMapping("/register")
    public JsonResult<?> register(
            @Valid @RequestBody AuthRegisterRequest request) throws AuthException {

        AuthServiceLoginResponse registerResponse = authService.register(AuthServiceRegisterRequest.of(request));

        return JsonResult.successOf(registerResponse);
    }

    @PostMapping("/delete")
    public JsonResult<?> userDelete(@AuthenticationPrincipal User user) {
        authService.userDelete(user.getUsername());

        return JsonResult.successOf();
    }

    @GetMapping("/update/{userId}")
    public JsonResult<?> updateUser(@AuthenticationPrincipal User user,
                                    @PathVariable(name = "userId") Long userId) {

        // 수정을 요청한 user와 현재 로그인한 user를 비교해 일치하는지 확인
        authService.authenticate(userId, user);

        // 수정 페이지에 필요한 정보를 조회해 반환
        return JsonResult.successOf(authService.updateUserPage(userId));
    }
}

