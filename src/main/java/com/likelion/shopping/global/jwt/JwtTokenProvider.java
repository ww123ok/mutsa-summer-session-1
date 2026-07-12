package com.likelion.shopping.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expirationTime;

    // application.yaml에 적어둔 jwt.secret과 jwt.expiration 값을 가져와서 열쇠(Key)로 변환
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration}") long expirationTime) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
    }

    // Access Token 발급 기능 (로그인 성공 시 호출됨!)
    public String createAccessToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime); // 현재시간 + 1시간 뒤 만료

        return Jwts.builder()
                .setSubject(email) // 토큰 주인 (이메일 저장)
                .setIssuedAt(now)  // 토큰 발급 시간
                .setExpiration(validity) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 우리 서버 열쇠로 암호화 서명!
                .compact();
    }

    // 토큰에서 이메일(Subject) 꺼내기 (나중에 API 요청 들어올 때 누군지 알아낼 때 씀)
    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰이 유효한지(위조되지 않았는지, 만료되지 않았는지) 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true; // 정상 토큰!
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false; // 비정상 토큰!
    }
}