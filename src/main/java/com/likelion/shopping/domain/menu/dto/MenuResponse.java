package com.likelion.shopping.domain.menu.dto;

import com.likelion.shopping.domain.menu.entity.Menu;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuResponse {

    private Long menuId;
    private String name;
    private Integer price;
    private String imageUrl;
    private boolean isMultiple;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .imageUrl(menu.getStore().getImageUrl())
                .isMultiple(menu.isMultiple())
                .build();
    }
}
