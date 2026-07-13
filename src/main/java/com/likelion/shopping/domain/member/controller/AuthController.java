package com.likelion.shopping.domain.member.controller;

import com.likelion.shopping.domain.member.dto.LoginRequest;
import com.likelion.shopping.domain.member.dto.SignupRequest;
import com.likelion.shopping.domain.member.dto.SignupResponse;
import com.likelion.shopping.domain.member.dto.TokenResponse;
import com.likelion.shopping.domain.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 로그인 API 추가! (성공 시 200 OK와 함께 JWT 토큰 반환)
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}