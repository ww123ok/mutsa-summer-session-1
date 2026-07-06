package com.likelion.shopping.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Store {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "image_url")
    private String imageUrl;

    public static Store create(String name, String category, Double rating, String imageUrl) {
        return Store.builder()
                .name(name)
                .category(category)
                .rating(rating != null ? rating : 0.0)
                .imageUrl(imageUrl)
                .build();
    }
}
