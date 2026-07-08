package com.likelion.shopping.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CartResponse {
    private List<StoreGroupResponse> storeGroups;

    public static CartResponse from(List<StoreGroupResponse> storeGroups) {
        return CartResponse.builder()
                .storeGroups(storeGroups)
                .build();
    }
}