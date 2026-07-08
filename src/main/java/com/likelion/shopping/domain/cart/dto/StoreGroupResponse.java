package com.likelion.shopping.domain.cart.dto;

import com.likelion.shopping.domain.store.entity.Store;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class StoreGroupResponse {
    private Long storeId;
    private String storeName;
    private List<CartItemResponse> items;

    public static StoreGroupResponse of(Store store, List<CartItemResponse> items) {
        return StoreGroupResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .items(items)
                .build();
    }
}