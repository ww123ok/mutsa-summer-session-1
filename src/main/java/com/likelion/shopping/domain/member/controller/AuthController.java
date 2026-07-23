package com.likelion.shopping.domain.member.controller;

import com.likelion.shopping.domain.member.dto.LoginRequest;
import com.likelion.shopping.domain.member.dto.SignupRequest;
import com.likelion.shopping.domain.member.dto.SignupResponse;
import com.likelion.shopping.domain.member.dto.TokenResponse;
import com.likelion.shopping.domain.member.service.AuthService;
import com.likelion.shopping.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse result = authService.signup(request);

        ApiResponse<SignupResponse> response =
                ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "회원가입 성공",
                        result
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        TokenResponse result = authService.login(request);

        ApiResponse<TokenResponse> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "로그인 성공",
                        result
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}