package com.likelion.shopping.domain.store.dto;

import com.likelion.shopping.domain.menu.dto.MenuResponse;
import com.likelion.shopping.domain.menu.entity.Menu;
import com.likelion.shopping.domain.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreDetailResponse {

    private Long storeId;
    private String name;
    private String category;
    private Double rating;
    private String imageUrl;
    private List<MenuResponse> menus;

    public static StoreDetailResponse of(Store store, List<Menu> menus) {
        return StoreDetailResponse.builder()
                .storeId(store.getId())
                .name(store.getName())
                .category(store.getCategory())
                .rating(store.getRating())
                .imageUrl(store.getImageUrl())
                .menus(menus.stream().map(MenuResponse::from).toList())
                .build();
    }
}
