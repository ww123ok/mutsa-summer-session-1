package com.likelion.shopping.domain.menu.dto;

import com.likelion.shopping.domain.menu.entity.Menu;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MenuResponse {

    private Long menuId;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;

    @JsonProperty("isMultiple")
    private boolean isMultiple;

    private List<MenuOptionResponse> options;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .imageUrl(menu.getStore().getImageUrl())
                .isMultiple(menu.isMultiple())
                .options(menu.getOptions().stream()
                        .map(MenuOptionResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}