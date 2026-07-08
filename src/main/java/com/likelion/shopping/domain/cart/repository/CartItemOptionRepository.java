package com.likelion.shopping.domain.cart.repository;

import com.likelion.shopping.domain.cart.entity.CartItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemOptionRepository extends JpaRepository<CartItemOption, Long> {
}