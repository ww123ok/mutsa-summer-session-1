package com.likelion.shopping.domain.store.dto;

import com.likelion.shopping.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreResponse {

    private Long storeId;
    private String name;
    private String category;
    private Double rating;
    private String imageUrl;

    // 엔티티에 객체를 넣으면 DTO로 변환해주는 메서드
    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getCategory(),
                store.getRating(),
                store.getImageUrl()
        );
    }
}
