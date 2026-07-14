package com.likelion.shopping.domain.menu.dto;

import com.likelion.shopping.domain.menu.entity.MenuOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuOptionResponse {

    private Long menuOptionId;
    private String name;
    private int additionalPrice;

    public static MenuOptionResponse from(MenuOption menuOption) {
        return MenuOptionResponse.builder()
                .menuOptionId(menuOption.getId())
                .name(menuOption.getName())
                .additionalPrice(menuOption.getAdditionalPrice())
                .build();
    }
}