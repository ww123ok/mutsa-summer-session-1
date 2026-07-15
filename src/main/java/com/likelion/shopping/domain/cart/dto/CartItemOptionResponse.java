package com.likelion.shopping.domain.cart.dto;

import com.likelion.shopping.domain.cart.entity.CartItemOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemOptionResponse {
    private Long menuOptionId;   // 💡 [명세서 일치] optionId -> menuOptionId
    private String name;         // 💡 [명세서 일치] optionName -> name
    private int additionalPrice; // 💡 [명세서 일치] optionPrice -> additionalPrice

    public static CartItemOptionResponse from(CartItemOption cartItemOption) {
        return CartItemOptionResponse.builder()
                .menuOptionId(cartItemOption.getMenuOption().getId())
                .name(cartItemOption.getMenuOption().getName())
                .additionalPrice(cartItemOption.getMenuOption().getAdditionalPrice())
                .build();
    }
}