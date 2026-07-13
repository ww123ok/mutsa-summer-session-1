package com.likelion.shopping.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String tokenType; // "Bearer" 라는 글자가 들어갑니다.

    public static TokenResponse of(String accessToken) {
        return new TokenResponse(accessToken, "Bearer");
    }
}