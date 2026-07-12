package com.likelion.shopping.domain.member.controller;

import com.likelion.shopping.domain.member.dto.UserResponse;
import com.likelion.shopping.domain.member.service.AuthService;
import com.likelion.shopping.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 내 정보 조회 API (GET /api/users/me)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@RequestHeader("Authorization") String authorizationHeader) {
        // 1. Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 401 에러 반환
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. "Bearer " 문자열(7글자) 이후의 진짜 토큰 값만 쏙 잘라내기
        String token = authorizationHeader.substring(7);

        // 3. 토큰 위조/만료 여부 검사
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 4. 검증된 토큰에서 이메일을 꺼내서 DB 조회 후 반환!
        String email = jwtTokenProvider.getEmail(token);
        UserResponse response = authService.getMe(email);

        return ResponseEntity.ok(response);
    }
}