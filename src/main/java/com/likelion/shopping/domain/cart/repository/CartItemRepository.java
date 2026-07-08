package com.likelion.shopping.domain.cart.repository;

import com.likelion.shopping.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}