package com.likelion.shopping.domain.cart.dto;

import com.likelion.shopping.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private String menuName;
    private int basePrice;       // 💡 [명세서 일치] price -> basePrice
    private int quantity;
    private int itemTotalPrice;  // 💡 [명세서 일치] totalPrice -> itemTotalPrice
    private List<CartItemOptionResponse> options;

    public static CartItemResponse from(CartItem cartItem) {
        List<CartItemOptionResponse> optionResponses = cartItem.getCartItemOptions().stream()
                .map(CartItemOptionResponse::from)
                .toList();

        int optionsTotalPrice = optionResponses.stream()
                .mapToInt(CartItemOptionResponse::getAdditionalPrice)
                .sum();

        int itemTotal = (cartItem.getMenu().getPrice() + optionsTotalPrice) * cartItem.getQuantity();

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .menuName(cartItem.getMenu().getName())
                .basePrice(cartItem.getMenu().getPrice())
                .quantity(cartItem.getQuantity())
                .itemTotalPrice(itemTotal)
                .options(optionResponses)
                .build();
    }
}