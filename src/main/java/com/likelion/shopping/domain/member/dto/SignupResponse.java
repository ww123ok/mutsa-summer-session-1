package com.likelion.shopping.domain.member.dto;

import com.likelion.shopping.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long id;
    private String email;
    private String name;

    // 엔티티를 바로 DTO로 변환해 주는 편의 메서드 (클린 코드)
    public static SignupResponse from(Member member) {
        return new SignupResponse(member.getId(), member.getEmail(), member.getName());
    }
}