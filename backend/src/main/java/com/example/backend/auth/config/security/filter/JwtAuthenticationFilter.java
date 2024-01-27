package com.example.backend.auth.config.security.filter;

import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.token.RefreshTokenService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.refreshToken.RefreshToken;
import com.example.backend.domain.define.refreshToken.repository.RefreshTokenRepository;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.constant.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/*
    Jwt 토큰 인증 필터
    - 사용자 인증 필터 전에 존재
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final static int ACCESS_TOKEN_INDEX = 1;

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info(">>>> [ Jwt 인증 필터에 진입합니다. ] <<<<");

        // 헤더에서 JWT 토큰 추출
        String jwtToken = request.getHeader("Authorization");
        String subject;

        // JWT 토큰이 헤더에 없다면 사용자 인증이 되지 않은 상태이므로 다음 인증 필터로 이동
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            log.info(">>>> [ Jwt 토큰이 헤더에 없으므로 다음 필터로 이동합니다 ] <<<<");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Token 구성: "Bearer {Access_Token} {Refresh_Token}"
            List<String> tokens = Arrays.asList(jwtToken.split(" "));

            // 공백(" ")으로 나눈 tokens: "Bearer"와 "{Access_Token}"와 "{Refresh_Token}"
            if (tokens.size() == 3) {
                // Access Token 추출
                String accessToken = tokens.get(ACCESS_TOKEN_INDEX);
                subject = jwtService.extractSubject(accessToken);

                // JWT 토큰 인증 로직 (JWT 검증 후 인증된 Authentication을 SecurityContext에 등록
                authenticateUserWithJwtToken(subject, accessToken, request);
                log.info(">>>> [ Jwt 토큰이 성공적으로 인증되었습니다. ] <<<<");

                // JWT 토큰 인증을 마치면 다음 인증 필터로 이동
                filterChain.doFilter(request, response);
            } else {
                jwtExceptionHandler(response, ExceptionMessage.JWT_INVALID_HEADER);
            }

        } catch (ExpiredJwtException e) {
            logger.error(ExceptionMessage.JWT_TOKEN_EXPIRED, e);

            // 헤더에서 토큰 추출
            List<String> tokens = Arrays.asList(jwtToken.split(" "));
            // refreshToken이 존재하는지 확인 - TTL로 만료시간 자동 체크
            RefreshToken refreshToken = refreshTokenRepository.findById(tokens.get(2)).orElseThrow(
                    () -> new JwtException(ExceptionMessage.JWT_NOT_EXIST_RTK));
            subject = jwtService.extractSubject(refreshToken.getRefreshToken());
            // 엑세스 토큰 재발급
            String reissueAccessToken = refreshTokenService.reissue(jwtService.extractAllClaims(refreshToken.getRefreshToken()), refreshToken.getRefreshToken());
            System.out.println(reissueAccessToken);
            // 재발급 받은 토큰 유효성 검증 후 시큐리티에 등록
            authenticateUserWithJwtToken(subject, reissueAccessToken, request);
            filterChain.doFilter(request, response);

        } catch (UnsupportedJwtException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_UNSUPPORTED);

        } catch (MalformedJwtException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_MALFORMED);

        } catch (SignatureException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_SIGNATURE);

        } catch (IllegalArgumentException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_ILLEGAL_ARGUMENT);

        } catch (JwtException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_SUBJECT_IS_NULL);
        }
    }

    private void authenticateUserWithJwtToken(String subject, String accessToken, HttpServletRequest request) {
        // subject이 null인 경우 예외 발생
        if (subject == null) {
            throw new JwtException(ExceptionMessage.JWT_SUBJECT_IS_NULL);
        }

        // JWT 토큰 검증
        if (jwtService.isTokenValid(accessToken, subject)) {
            Claims claims = jwtService.extractAllClaims(accessToken);

             /*
                 subject에서 platformId와 platformType 추출
                 * Security Context에 저장되는 Authentication은 딱히 platformId와 platformType 유의미해 보이진 않지만
                 * getUsername() 메서드가 시큐리티의 다른 필터들에서도 계속 사용되어
                 * 없으면 NullPointerException이 발생해서 로직을 추가했습니다.
              */
            String[] platformIdAndPlatformType = extractFromSubject(subject);
            String platformId = platformIdAndPlatformType[0];
            String platformType = platformIdAndPlatformType[1];

            // JWT 토큰의 Claim을 사용해 User 생성
            UserDetails userDetails = User.builder()
                    .role(UserRole.valueOf(claims.get("role", String.class)))
                    .name(claims.get("name", String.class))
                    .profileImageUrl(claims.get("profileImageUrl", String.class))
                    .platformId(platformId)
                    .platformType(UserPlatformType.valueOf(platformType))
                    .build();

            // UserDetails를 사용해 Authentication 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // Authentication에 현재 요청 정보를 저장
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


            // Security Context에 Authentication 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String[] extractFromSubject(String subject) {
        // "_"로 문자열을 나누고 id와 type을 추출
        // 이미 검증된 토큰이므로 따로 예외처리 필요 없음
        return subject.split("_");
    }

    // 모든 JWT Exception을 처리하는 핸들러
    private void jwtExceptionHandler(HttpServletResponse response, ExceptionMessage message) throws IOException {
        log.error(">>>> [ JWT 토큰 인증 중 Error 발생 : {} ] <<<<", message.getText());

        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(JsonResult.failOf(message.getText())));
    }

}
