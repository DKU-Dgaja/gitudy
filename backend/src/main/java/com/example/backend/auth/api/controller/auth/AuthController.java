package com.example.backend.auth.api.controller.auth;

import com.example.backend.auth.api.controller.auth.request.AdminLoginRequest;
import com.example.backend.auth.api.controller.auth.request.AuthRegisterRequest;
import com.example.backend.auth.api.controller.auth.request.UserNameRequest;
import com.example.backend.auth.api.controller.auth.request.UserUpdateRequest;
import com.example.backend.auth.api.controller.auth.response.*;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.request.UserUpdateServiceRequest;
import com.example.backend.auth.api.service.auth.response.UserUpdatePageResponse;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.state.LoginStateService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.common.exception.oauth.OAuthException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.study.api.controller.member.request.MessageRequest;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final static int TOKEN_INDEX = 7;
    private final static String AUTHORIZATION = "Authorization";
    private final static String BEARER = "Bearer ";
    private final AuthService authService;
    private final OAuthService oAuthService;
    private final LoginStateService loginStateService;


    @ApiResponse(responseCode = "200", description = "로그인페이지 요청 성공", content = @Content(schema = @Schema(implementation = AuthLoginPageResponse.class)))
    @GetMapping("/loginPage")
    public ResponseEntity<List<AuthLoginPageResponse>> loginPage() {

        String loginState = loginStateService.generateLoginState();

        // OAuth 사용하여 각 플랫폼의 로그인 페이지 URL을 가져와서 state 주입
        List<AuthLoginPageResponse> loginPages = oAuthService.loginPage(loginState);

        return ResponseEntity.ok().body(loginPages);
    }

    @ApiResponse(responseCode = "200", description = "로그인 요청 성공", content = @Content(schema = @Schema(implementation = AuthLoginResponse.class)))
    @GetMapping("/{platformType}/login")
    public ResponseEntity<AuthLoginResponse> login(@PathVariable("platformType") UserPlatformType platformType,
                                                   @RequestParam("code") String code,
                                                   @RequestParam("state") String loginState) {

        // state 값이 유효한지 검증
        if (!loginStateService.isValidLoginState(loginState)) {
            throw new OAuthException(ExceptionMessage.LOGINSTATE_INVALID_VALUE);
        }

        AuthLoginResponse loginResponse = authService.login(platformType, code, loginState);

        return ResponseEntity.ok().body(loginResponse);
    }

    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(TOKEN_INDEX);

            authService.logout(token);

            return ResponseEntity.ok().build();
        } else {
            log.warn(">>>> Invalid Header Access : {}", ExceptionMessage.JWT_INVALID_HEADER.getText());
            throw new JwtException(ExceptionMessage.JWT_INVALID_HEADER);
        }

    }

    // JWT 토큰이 만료되었을 때 재발급을 처리할 컨트롤러
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = ReissueAccessTokenResponse.class)))
    @PostMapping("/reissue")
    public ResponseEntity<ReissueAccessTokenResponse> reissueAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(TOKEN_INDEX);

            ReissueAccessTokenResponse reissueResponse = authService.reissueAccessToken(token);

            return ResponseEntity.ok().body(reissueResponse);
        } else {
            log.warn(">>>> Invalid Header Access : {}", ExceptionMessage.JWT_INVALID_HEADER.getText());
            throw new JwtException(ExceptionMessage.JWT_INVALID_HEADER);
        }
    }

    @ApiResponse(responseCode = "200", description = "회원정보 조회 성공", content = @Content(schema = @Schema(implementation = UserInfoAndRankingResponse.class)))
    @GetMapping("/info")
    public ResponseEntity<UserInfoAndRankingResponse> userInfo(@AuthenticationPrincipal User user) {

        UserInfoAndRankingResponse userInfoResponse = authService.getUserByInfo(user.getPlatformId(), user.getPlatformType());

        return ResponseEntity.ok().body(userInfoResponse);
    }

    @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = AuthLoginResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<AuthLoginResponse> register(@AuthenticationPrincipal User user,
                                                      @Valid @RequestBody AuthRegisterRequest request) throws AuthException {

        AuthLoginResponse response = authService.register(AuthServiceRegisterRequest.of(request), user);

        return ResponseEntity.ok().body(response);
    }

    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    @PostMapping("/delete")
    public ResponseEntity<Void> userDelete(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody MessageRequest request) {

        authService.userDelete(user, request.getMessage());

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "회원정보 수정 페이지 요청 성공", content = @Content(schema = @Schema(implementation = UserUpdatePageResponse.class)))
    @GetMapping("/update")
    public ResponseEntity<UserUpdatePageResponse> updateUser(@AuthenticationPrincipal User user) {

        // Jwt 토큰을 이용해 유저 정보 추출
        UserInfoResponse userInfo = authService.findUserInfo(user);

        // 수정 페이지에 필요한 정보를 조회해 반환
        return ResponseEntity.ok().body(authService.updateUserPage(userInfo.getUserId()));
    }

    @ApiResponse(responseCode = "200", description = "회원정보 수정 요청 성공")
    @PostMapping("/update")
    public ResponseEntity<Void> updateUser(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody UserUpdateRequest request) {

        // Jwt 토큰을 이용해 유저 정보 추출
        UserInfoResponse userInfo = authService.findUserInfo(user);

        // 회원 정보 수정
        authService.updateUser(UserUpdateServiceRequest.of(userInfo.getUserId(), request));

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "푸시 알림 여부 수정 요청 성공")
    @GetMapping("/update/pushAlarmYn/{pushAlarmEnable}")
    public ResponseEntity<Void> updatePushAlarmYn(@AuthenticationPrincipal User user,
                                                  @PathVariable(name = "pushAlarmEnable") boolean pushAlarmEnable) {

        // Jwt 토큰을 이용해 유저 정보 추출
        UserInfoResponse userInfo = authService.findUserInfo(user);

        // 푸시 알람 여부 수정
        authService.updatePushAlarmYn(userInfo.getUserId(), pushAlarmEnable);

        return ResponseEntity.ok().build();
    }


    @ApiResponse(responseCode = "200", description = "닉네임 중복 검사 성공")
    @PostMapping("/check-nickname")
    public ResponseEntity<Void> nickNameDuplicationCheck(@Valid @RequestBody UserNameRequest request) {

        authService.nickNameDuplicationCheck(request);

        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "관리자 로그인 성공")
    @PostMapping("/admin")
    public ResponseEntity<AuthLoginResponse> loginAdmin(@RequestBody AdminLoginRequest request) {

        AuthLoginResponse response = authService.loginAdmin(request);

        return ResponseEntity.ok().body(response);
    }
}
