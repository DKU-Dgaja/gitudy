package com.example.backend.auth.api.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final long HOUR = 1000 * 60 * 60;
    private static final long DAY = HOUR * 24;

    @Value("${jwt.secretKey}")
    private String secretKey;

    /*
        JWT AccessToken 생성
    */
    public String generateAccessToken(Map<String, String> customClaims, UserDetails userDetails) {
        // JWT AccessToken의 만료 시간을 1일로 설정한다.
        return generateAccessToken(customClaims, userDetails, new Date(System.currentTimeMillis() + DAY));
    }

    public String generateAccessToken(Map<String, String> customClaims, UserDetails userDetails, Date expiredTime) {
        return Jwts.builder()
                .setClaims(customClaims)
                .setSubject(userDetails.getUsername())  // User의 식별자
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredTime)
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     *   RefreshToken 생성
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails, new Date(System.currentTimeMillis() + 7 * DAY));
    }

    public String generateRefreshToken(Map<String, String> extraClaims, UserDetails userDetails) {
        return generateRefreshToken(extraClaims, userDetails, new Date(System.currentTimeMillis() + 7 * DAY));
    }

    public String generateRefreshToken(Map<String, String> extraClaims, UserDetails userDetails, Date expiredTime) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredTime)
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*
        JWT 토큰 정보 추출
    */
    // 모든 Claim 추출
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 특정 Claim 추출
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    // sub 추출
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 만료 일자 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /*
        JWT 토큰 검증
    */
    public boolean isTokenValid(String token, String username) {
        Claims claims = extractAllClaims(token);

        if (!claims.containsKey("role")) return false;
        if (!claims.containsKey("platformId")) return false;
        if (!claims.containsKey("platformType")) return false;

        String subject = claims.getSubject();
        return (subject.equals(username)) && !isTokenExpired(token);
    }

    // JWT 토큰이 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    // JWT 서명에 사용할 키 획득
    private Key getSignInkey() {
        // Base64로 암호화(인코딩)되어 있는 secretKey를 바이트 배열로 복호화(디코딩)
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);

        // JWT 서명을 위해 HMAC 알고리즘 적용
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
