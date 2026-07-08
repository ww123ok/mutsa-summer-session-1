package com.likelion.shopping.domain.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemQuantityRequest {
    private int quantity;
}