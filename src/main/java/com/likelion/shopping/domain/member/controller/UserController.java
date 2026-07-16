package com.likelion.shopping.domain.member.controller;

import com.likelion.shopping.domain.member.dto.UserResponse;
import com.likelion.shopping.domain.member.service.AuthService;
import com.likelion.shopping.global.config.CustomUserDetails;
import com.likelion.shopping.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = authService.getMe(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(200, "내 정보 조회 성공", response));
    }
}