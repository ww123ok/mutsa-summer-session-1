package com.likelion.shopping.domain.cart.repository;

import com.likelion.shopping.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 💡 특정 장바구니에 특정 메뉴로 담긴 아이템 목록을 조회하는 메서드 추가!
    List<CartItem> findAllByCartIdAndMenuId(Long cartId, Long menuId);
}