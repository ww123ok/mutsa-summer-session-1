package com.likelion.shopping.domain.cart.controller;

import com.likelion.shopping.domain.cart.dto.*;
import com.likelion.shopping.domain.cart.service.CartService;
import com.likelion.shopping.global.config.CustomUserDetails;
import com.likelion.shopping.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Void>> addCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CartItemRequest request) {
        cartService.addCartItem(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(201, "장바구니에 메뉴가 성공적으로 담겼습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCartList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponse cartResponse = cartService.getCartList(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(200, "장바구니 목록 조회 성공", cartResponse));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemQuantityRequest request) {
        cartService.updateCartItemQuantity(userDetails.getId(), cartItemId, request);
        return ResponseEntity.ok(ApiResponse.of(200, "장바구니 상품 수량이 변경되었습니다."));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId) {
        cartService.deleteCartItem(userDetails.getId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.of(200, "장바구니 상품이 삭제되었습니다."));
    }
}