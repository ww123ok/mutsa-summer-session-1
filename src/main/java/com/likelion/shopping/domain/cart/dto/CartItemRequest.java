package com.likelion.shopping.domain.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class CartItemRequest {
    private Long menuId;
    private int quantity;
    private List<Long> menuOptionIds;
}