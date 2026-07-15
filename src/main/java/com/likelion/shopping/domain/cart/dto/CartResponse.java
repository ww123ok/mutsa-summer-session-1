package com.likelion.shopping.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CartResponse {
    private Long cartId;       // 💡 [명세서 일치] 장바구니 ID 추가!
    private int totalPrice;    // 💡 [명세서 일치] 장바구니 전체 결제 총액 (Grand Total) 추가!
    private List<StoreGroupResponse> stores; // 💡 [명세서 일치] storeGroups -> stores 로 변경!

    public static CartResponse of(Long cartId, int totalPrice, List<StoreGroupResponse> stores) {
        return CartResponse.builder()
                .cartId(cartId)
                .totalPrice(totalPrice)
                .stores(stores)
                .build();
    }
}