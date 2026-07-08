package com.likelion.shopping.domain.menu.repository;

import com.likelion.shopping.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}