package com.likelion.shopping.domain.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemQuantityRequest {

    @Min(value = 1, message = "장바구니 상품 수량은 1개 이상이어야 합니다.")
    private int quantity;
}