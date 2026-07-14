package com.likelion.shopping.domain.menu.entity;

import com.likelion.shopping.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

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

    @Column(nullable = false)
    private boolean isMultiple;

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MenuOption> options = new ArrayList<>();

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
