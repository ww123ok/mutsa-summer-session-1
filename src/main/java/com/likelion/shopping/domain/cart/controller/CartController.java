package com.likelion.shopping.domain.cart.controller;

import com.likelion.shopping.domain.cart.dto.CartItemQuantityRequest;
import com.likelion.shopping.domain.cart.dto.CartItemRequest;
import com.likelion.shopping.domain.cart.dto.CartResponse;
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

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCartList(
            @RequestHeader("Member-Id") Long memberId) {

        CartResponse cartResponse = cartService.getCartList(memberId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value()); // 200
        response.put("message", "장바구니 목록 조회 성공");
        response.put("data", cartResponse);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @RequestHeader("Member-Id") Long memberId,
            @PathVariable Long cartItemId,
            @RequestBody CartItemQuantityRequest request) {

        cartService.updateCartItemQuantity(memberId, cartItemId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value()); // 200
        response.put("message", "장바구니 상품 수량이 변경되었습니다.");

        return ResponseEntity.ok(response);
    }
}