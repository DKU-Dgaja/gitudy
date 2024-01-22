package com.example.backend.auth.api.service.token;


import com.example.backend.auth.api.service.jwt.JwtService;

import com.example.backend.domain.define.refreshToken.RefreshToken;
import com.example.backend.domain.define.refreshToken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    final RefreshTokenRepository refreshTokenRepository;
    final JwtService jwtService;

    // 리프레시 토큰 저장 메서드
    public void saveRefreshToken(RefreshToken refreshToken) {
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info(">>>> Refresh Token register : {}", savedToken.getRefreshToken());
    }
}
