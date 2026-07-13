package com.likelion.shopping.domain.member.controller;

import com.likelion.shopping.domain.member.dto.UserResponse;
import com.likelion.shopping.domain.member.service.AuthService;
import com.likelion.shopping.global.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    // 내 정보 조회 API (GET /api/users/me)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 시큐리티가 인증된 사용자의 정보를 CustomUserDetails에 담아서 넘김
        // 이메일만 꺼내서 바로 서비스로 보냄
        String email = userDetails.getUsername();

        UserResponse response = authService.getMe(email);

        return ResponseEntity.ok(response);
    }
}