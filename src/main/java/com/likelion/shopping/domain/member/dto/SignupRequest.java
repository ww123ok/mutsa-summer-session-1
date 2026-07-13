package com.likelion.shopping.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String name; // 엔티티와 동일하게 name으로 맞춤
}