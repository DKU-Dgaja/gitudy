package com.example.backend.auth.api.service.auth;

import com.example.backend.auth.api.controller.auth.request.UserNameRequest;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.controller.auth.response.ReissueAccessTokenResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoAndRankingResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.request.UserUpdateServiceRequest;
import com.example.backend.auth.api.service.auth.response.UserUpdatePageResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.jwt.JwtToken;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.api.service.rank.response.UserRankingResponse;
import com.example.backend.auth.api.service.token.RefreshTokenService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.fcm.repository.FcmTokenRepository;
import com.example.backend.domain.define.refreshToken.RefreshToken;
import com.example.backend.domain.define.refreshToken.repository.RefreshTokenRepository;
import com.example.backend.study.api.service.github.GithubApiTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private static final String PLATFORM_ID_CLAIM = "platformId";
    private static final String PLATFORM_TYPE_CLAIM = "platformType";
    private static final String ROLE_CLAIM = "role";
    private final UserRepository userRepository;
    private final OAuthService oAuthService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RankingService rankingService;
    private final FcmTokenRepository fcmTokenRepository;
    private final GithubApiTokenService githubApiTokenService;

    @Transactional
    public AuthLoginResponse login(UserPlatformType platformType, String code, String state) {
        OAuthResponse loginResponse = oAuthService.login(platformType, code, state);
        String name = loginResponse.getName();
        String profileImageUrl = loginResponse.getProfileImageUrl();
        String platformId = loginResponse.getPlatformId();

        log.info(">>>> [ {}님이 로그인하셨습니다 ] <<<<", name);

        /*
         * OAuth 로그인 인증을 마쳤으니 우리 애플리케이션의 DB에도 존재하는 사용자인지 확인한다.
         * 회원이 아닐 경우, 즉 회원가입이 필요한 신규 사용자의 경우 OAuthResponse를 바탕으로 DB에 등록해준다.
         */
        User findUser = userRepository.findByPlatformIdAndPlatformType(platformId, platformType)
                .orElseGet(() -> {
                    User saveUser = User.builder()
                            .platformId(platformId)
                            .platformType(loginResponse.getPlatformType())
                            .role(UserRole.UNAUTH)
                            .name(name)
                            .score(10)
                            .profileImageUrl(profileImageUrl)
                            .build();

                    log.info(">>>> [ UNAUTH 권한으로 사용자를 DB에 등록합니다. 이후 회원가입이 필요합니다 ] <<<<");

                    User user = userRepository.save(saveUser);

                    // 깃허브 api 토큰 저장
                    githubApiTokenService.saveToken(loginResponse.getGithubApiToken(), user.getId());

                    return user;
                });

        /*
            DB에 저장된 사용자 정보를 기반으로 JWT 토큰을 발급
            * JWT 토큰을 요청시에 담아 보내면 JWT 토큰 인증 필터에서 Security Context에 인증된 사용자로 등록
            TODO : JWT 재발급을 위한 Refresh 토큰은 Redis에서 관리할 예정입니다.
         */
        JwtToken jwtToken = generateJwtToken(findUser);

        // JWT 토큰과 권한 정보를 담아 반환
        return AuthLoginResponse.builder()
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .role(findUser.getRole())
                .build();
    }

    private JwtToken generateJwtToken(User user) {
        // JWT 토큰 생성을 위한 claims 생성
        HashMap<String, String> claims = new HashMap<>();
        claims.put(ROLE_CLAIM, user.getRole().name());
        claims.put(PLATFORM_ID_CLAIM, user.getPlatformId());
        claims.put(PLATFORM_TYPE_CLAIM, String.valueOf(user.getPlatformType()));


        // 사용자의 Refresh Token이 이미 존재하면 토큰 삭제
        Optional<RefreshToken> existingToken = refreshTokenRepository.findBySubject(user.getUsername());
        existingToken.ifPresent(refreshTokenRepository::delete);

        // Access Token 생성
        final String jwtAccessToken = jwtService.generateAccessToken(claims, user);

        // Refresh Token 생성
        final String jwtRefreshToken = jwtService.generateRefreshToken(claims, user);
        log.info(">>>> [ 사용자 {}님의 JWT 토큰이 발급되었습니다 ] <<<<", user.getName());
        log.info(">>>> [ 사용자 {}님의 refresh 토큰이 발급되었습니다 ] <<<<", user.getName());


        // Refresh Token을 레디스에 저장
        RefreshToken refreshToken = RefreshToken.builder().refreshToken(jwtRefreshToken).subject(user.getUsername()).build();
        refreshTokenService.saveRefreshToken(refreshToken);
        return JwtToken.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.logout(refreshToken);
    }

    // JWT 토큰이 만료되었을 때 재발급을 위한 서비스 로직
    public ReissueAccessTokenResponse reissueAccessToken(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);
        if (jwtService.isTokenValid(refreshToken, claims.getSubject())) {
            // 리프레시 토큰을 이용해 새로운 엑세스 토큰 발급
            String accessToken = refreshTokenService.reissue(claims, refreshToken);
            log.info(">>>> {} reissue AccessToken.", claims.getSubject());

            return ReissueAccessTokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } else {
            log.warn(">>>> Token Validation Fail : {}", ExceptionMessage.JWT_INVALID_RTK.getText());
            throw new JwtException(ExceptionMessage.JWT_INVALID_RTK);
        }
    }

    public UserInfoAndRankingResponse getUserByInfo(String platformId, UserPlatformType platformType) {

        User user = userRepository.findByPlatformIdAndPlatformType(platformId, platformType)
                .orElseThrow(() -> {
                    log.warn(">>>> User not found with platformId: {} platformType: {}", platformId, platformType);
                    throw new UserException(ExceptionMessage.USER_NOT_FOUND);
                });

        UserRankingResponse rankingResponse = rankingService.getUserRankings(user);

        return UserInfoAndRankingResponse.of(user, rankingResponse.getRanking());
    }

    @Transactional
    public AuthLoginResponse register(AuthServiceRegisterRequest request, User user) {
        User findUser = userRepository.findByPlatformIdAndPlatformType(user.getPlatformId(), user.getPlatformType()).orElseThrow(() -> {
            // UNAUTH인 토큰을 받고 회원 탈퇴 후 그 토큰으로 회원가입 요청시 예외 처리
            log.warn(">>>> User Not Exist : {}", ExceptionMessage.AUTH_INVALID_REGISTER.getText());
            return new AuthException(ExceptionMessage.AUTH_INVALID_REGISTER);
        });

        // UNAUTH 토큰으로 회원가입을 요청했지만 이미 update되어 UNAUTH가 아닌 사용자 예외 처리
        if (findUser.getRole() != UserRole.UNAUTH) {
            log.warn(">>>> Not UNAUTH User : {}", ExceptionMessage.AUTH_DUPLICATE_UNAUTH_REGISTER.getText());
            throw new AuthException(ExceptionMessage.AUTH_DUPLICATE_UNAUTH_REGISTER);
        }

        // 회원가입 정보 DB 반영
        findUser.updateRegister(request.getName(), request.getGithubId(), request.isPushAlarmYn());

        // fcmToken 저장
        FcmToken fcmToken = FcmToken.builder()
                .userId(user.getId())
                .fcmToken(request.getFcmToken())
                .build();
        fcmTokenRepository.save(fcmToken);

        // JWT Access Token, Refresh Token 재발급
        JwtToken tokens = generateJwtToken(findUser);

        return AuthLoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .role(findUser.getRole())
                .build();
    }

    @Transactional
    public void userDelete(String userName) {
        String[] platformIdAndPlatformType = extractFromSubject(userName);
        String platformId = platformIdAndPlatformType[0];
        String platformType = platformIdAndPlatformType[1];
        User user = userRepository.findByPlatformIdAndPlatformType(platformId, UserPlatformType.valueOf(platformType)).orElseThrow(() -> {
            log.warn(">>>> User Delete Fail : {}", ExceptionMessage.AUTH_NOT_FOUND.getText());
            return new AuthException(ExceptionMessage.AUTH_NOT_FOUND);
        });

        try {
            user.deleteUser();
            log.info(">>>> {} Info is Deleted.", user.getName());
        } catch (IllegalArgumentException e) {
            log.error(">>>> ID = {} : 계정 삭제에 실패했습니다.", user.getId());
            throw new AuthException(ExceptionMessage.AUTH_DELETE_FAIL);
        }
    }

    private String[] extractFromSubject(String subject) {
        // "_"로 문자열을 나누고 id와 type을 추출
        return subject.split("_");
    }

    public UserInfoResponse authenticate(Long userId, User user) {
        User findUser = userRepository.findByPlatformIdAndPlatformType(user.getPlatformId(), user.getPlatformType())
                .orElseThrow(() -> {
                    log.error(">>>> User not found for platformId {} and platformType {} <<<<", user.getPlatformId(), user.getPlatformType());
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });

        // 로그인된 사용자의 ID와 수정을 요청한 회원 정보의 ID와 비교
        if (findUser.getId() != userId) {
            log.error(">>>> User ID {} does not match the requested user ID {} <<<<", findUser.getId(), userId);
            throw new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY);
        }

        return UserInfoResponse.of(findUser);
    }

    public UserUpdatePageResponse updateUserPage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND.getText());
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });

        return UserUpdatePageResponse.of(user);
    }

    @Transactional
    public void updateUser(UserUpdateServiceRequest request) {
        Long userId = request.getUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND.getText());
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });

        user.updateUser(request.getName(),
                request.getProfileImageUrl(),
                request.isProfilePublicYn(),
                request.getSocialInfo());
    }

    @Transactional
    public void updatePushAlarmYn(Long userId, boolean pushAlarmEnable) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND.getText());
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });

        user.updatePushAlarmYn(pushAlarmEnable);
    }

    public UserInfoResponse findUserInfo(User contextUser) {
        User findUser = userRepository.findByPlatformIdAndPlatformType(contextUser.getPlatformId(), contextUser.getPlatformType())
                .orElseThrow(() -> {
                    log.error(">>>> User not found for platformId {} and platformType {} <<<<", contextUser.getPlatformId(), contextUser.getPlatformType());
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });

        return UserInfoResponse.of(findUser);
    }

    // 닉네임 중복체크 메서드
    public void nickNameDuplicationCheck(UserNameRequest request) {

        boolean exists = userRepository.existsByName(request.getName());

        if (exists) {
            log.warn(">>>> {} : {} <<<<", request.getName(), ExceptionMessage.USER_NAME_DUPLICATION);
            throw new UserException(ExceptionMessage.USER_NAME_DUPLICATION);
        }

    }

    public Long findUserIdByGithubIdOrElseThrowException(String githubId) {
        return userRepository.findByGithubId(githubId).orElseThrow(() -> {
            log.error(">>>> User not found for githubId {} <<<<", githubId);
            return new UserException(ExceptionMessage.USER_NOT_FOUND_WITH_GITHUB_ID);
        }).getId();
    }
}