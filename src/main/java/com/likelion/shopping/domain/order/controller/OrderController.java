package com.likelion.shopping.domain.order.controller;

import com.likelion.shopping.domain.order.dto.OrderCreateResponse;
import com.likelion.shopping.domain.order.service.OrderService;
import com.likelion.shopping.global.config.CustomUserDetails;
import com.likelion.shopping.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> checkoutOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 💡 수정!

        // 💡 userDetails.getId()를 서비스에 넘겨줘!
        OrderCreateResponse response = orderService.checkoutOrder(userDetails.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "주문 및 결제가 완료되었습니다!", response));
    }
}
