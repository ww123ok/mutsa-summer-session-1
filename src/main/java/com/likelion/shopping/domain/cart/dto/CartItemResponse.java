package com.likelion.shopping.domain.cart.dto;

import com.likelion.shopping.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private String menuName;
    private int quantity;
    private int price; // 메뉴 기본 가격

    public static CartItemResponse from(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .menuName(cartItem.getMenu().getName())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getMenu().getPrice())
                .build();
    }
}