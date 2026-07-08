package com.likelion.shopping.domain.cart.controller;

import com.likelion.shopping.domain.cart.dto.CartItemRequest;
import com.likelion.shopping.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addCartItem(
            @RequestHeader("Member-Id") Long memberId,
            @RequestBody CartItemRequest request) {

        cartService.addCartItem(memberId, request);

        // 공통 응답 포맷 생성 (status, message)
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value()); // 201
        response.put("message", "장바구니에 메뉴가 성공적으로 담겼습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}