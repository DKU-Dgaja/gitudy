package com.example.backend.auth.config.security.filter;

import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.config.security.auth.CustomUserDetails;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.constant.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info(">>>> [ Jwt 인증 필터에 진입합니다. ] <<<<");

        // 헤더에서 JWT 토큰 추출
        String jwtToken = request.getHeader("Authorization");
        String userEmail;

        // JWT 토큰이 헤더에 없다면 사용자 인증이 되지 않은 상태이므로 다음 인증 필터로 이동
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            List<String> tokens = Arrays.asList(jwtToken.split(" "));

            if (tokens.size() == 2) {
                // Access Token 추출
                String accessToken = tokens.get(1);
                userEmail = jwtService.extractSubject(accessToken);

                // JWT 토큰 인증 로직 (JWT 검증 후 인증된 Authentication을 SecurityContext에 등록
                authenticateUserWithJwtToken(userEmail, accessToken, request);
                log.info(">>>> [ Jwt 토큰이 성공적으로 인증되었습니다. ] <<<<");

                // JWT 토큰 인증을 마치면 다음 인증 필터로 이동
                filterChain.doFilter(request, response);
            } else {
                jwtExceptionHandler(response, ExceptionMessage.JWT_INVALID_HEADER);
            }

        } catch (ExpiredJwtException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_TOKEN_EXPIRED);

        } catch (UnsupportedJwtException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_UNSUPPORTED);

        } catch (MalformedJwtException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_MALFORMED);

        } catch (SignatureException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_SIGNATURE);

        } catch (IllegalArgumentException e) {
            jwtExceptionHandler(response, ExceptionMessage.JWT_ILLEGAL_ARGUMENT);

        }


    }

    private void authenticateUserWithJwtToken(String userEmail, String accessToken, HttpServletRequest request) {
        // JWT 토큰 검증
        if (userEmail != null && jwtService.isTokenValid(accessToken, userEmail)) {
            Claims claims = jwtService.extractAllClaims(accessToken);

            // JWT 토큰의 Claim을 사용해 User를 생성한 후 UserDetails 타입으로 데코레이트
            UserDetails userDetails = new CustomUserDetails(User.builder()
                    .email(userEmail)
                    .role(UserRole.valueOf(claims.get("role", String.class)))
                    .name(claims.get("name", String.class))
                    .profileImageUrl(claims.get("profileImageUrl", String.class))
                    .build());

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

    // 모든 JWT Exception을 처리하는 핸들러
    private void jwtExceptionHandler(HttpServletResponse response, ExceptionMessage message) throws IOException {
        log.error(">>>> [ JWT Error : {} ] <<<<", message.getText());

        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(JsonResult.failOf(message.getText())));
    }

}
