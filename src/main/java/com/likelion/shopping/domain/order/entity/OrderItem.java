package com.likelion.shopping.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    private Orders orders;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int quantity;

    @Builder.Default
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<OrderItemOption> orderItemOptions = new ArrayList<>();

    public static OrderItem create(Orders orders, Long menuId, String menuName, int orderPrice, int quantity) {
        return OrderItem.builder()
                .orders(orders)
                .menuName(menuName)
                .orderPrice(orderPrice)
                .quantity(quantity)
                .build();
    }

    public void addOption(OrderItemOption option) {
        this.orderItemOptions.add(option);
    }
}
