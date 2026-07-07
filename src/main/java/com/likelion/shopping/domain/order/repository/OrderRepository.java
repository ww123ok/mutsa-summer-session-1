package com.likelion.shopping.domain.order.repository;

import com.likelion.shopping.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}