package com.likelion.shopping.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {
    private Long orderId;
    private int totalPrice;
    private int remainCredit;
}