package com.likelion.shopping.domain.member.dto;

import com.likelion.shopping.domain.cart.dto.CartResponse;
import com.likelion.shopping.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private int credit;
    private CartResponse cart;

    public static UserResponse of(Member member, CartResponse cart) {
        return new UserResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getCredit(),
                cart
        );
    }
}