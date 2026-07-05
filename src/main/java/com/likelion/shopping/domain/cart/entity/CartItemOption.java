package com.likelion.shopping.domain.cart.entity;

import com.likelion.shopping.domain.menu.entity.MenuOption;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CartItemOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_id", nullable = false)
    private CartItem cartItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_id", nullable = false)
    private MenuOption menuOption;

    public static CartItemOption create(CartItem cartItem, MenuOption menuOption) {
        return CartItemOption.builder()
                .cartItem(cartItem)
                .menuOption(menuOption)
                .build();
    }
}
