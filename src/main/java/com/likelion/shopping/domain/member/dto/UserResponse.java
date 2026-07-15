package com.likelion.shopping.domain.member.dto;

import com.likelion.shopping.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    //크래딧 추가
    private int credit;

    // Member 엔티티를 DTO로 변환해 주는 센스 있는 팩토리 메서드
    public static UserResponse from(Member member) {
        return new UserResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getCredit()
        );
    }
}