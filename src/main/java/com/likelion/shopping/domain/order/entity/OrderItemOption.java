package com.likelion.shopping.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderItemOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "option_name", nullable = false, length = 100)
    private String optionName;

    @Column(name = "option_price", nullable = false)
    private int optionPrice;

    public static OrderItemOption create(OrderItem orderItem, String optionName, int optionPrice) {
        return OrderItemOption.builder()
                .orderItem(orderItem)
                .optionName(optionName)
                .optionPrice(optionPrice)
                .build();
    }
}
