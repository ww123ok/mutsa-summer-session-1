package com.likelion.shopping.domain.cart.controller;

import com.likelion.shopping.domain.cart.dto.CartItemQuantityRequest;
import com.likelion.shopping.domain.cart.dto.CartItemRequest;
import com.likelion.shopping.domain.cart.dto.CartResponse;
import com.likelion.shopping.domain.cart.service.CartService;
import com.likelion.shopping.global.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @AuthenticationPrincipal CustomUserDetails userDetails, // 💡 토큰에서 정보 가져오기
            @RequestBody CartItemRequest request) {

        cartService.addCartItem(userDetails.getId(), request); // 💡 getId()로 숫자 뽑기

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "장바구니에 메뉴가 성공적으로 담겼습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCartList(
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 💡 수정

        CartResponse cartResponse = cartService.getCartList(userDetails.getId()); // 💡 수정

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "장바구니 목록 조회 성공");
        response.put("data", cartResponse);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 💡 수정
            @PathVariable Long cartItemId,
            @RequestBody CartItemQuantityRequest request) {

        cartService.updateCartItemQuantity(userDetails.getId(), cartItemId, request); // 💡 수정

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "장바구니 상품 수량이 변경되었습니다.");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> deleteCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 💡 수정
            @PathVariable Long cartItemId) {

        cartService.deleteCartItem(userDetails.getId(), cartItemId); // 💡 수정

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "장바구니 상품이 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }
}