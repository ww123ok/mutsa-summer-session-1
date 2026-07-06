package com.likelion.shopping.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MenuOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "additional_price", nullable = false)
    private int additionalPrice;

    public static MenuOption create(Menu menu, String name, int additionalPrice) {
        return MenuOption.builder()
                .menu(menu)
                .name(name)
                .additionalPrice(additionalPrice)
                .build();
    }
}
