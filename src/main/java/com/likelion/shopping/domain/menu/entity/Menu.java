package com.likelion.shopping.domain.menu.entity;

import com.likelion.shopping.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Menu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(length = 255)
    private String description;

    // 옵션 복수 선택 가능 여부 추가 (true: 복수 선택 / false: 단수 선택)
    @Column(nullable = false)
    private boolean isMultiple;

    public static Menu create(Store store, String name, int price, String description, boolean isMultiple) {
        return Menu.builder()
                .store(store)
                .name(name)
                .price(price)
                .description(description)
                .isMultiple(isMultiple)
                .build();
    }
}
