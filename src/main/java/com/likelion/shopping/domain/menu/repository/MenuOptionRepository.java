package com.likelion.shopping.domain.menu.repository;

import com.likelion.shopping.domain.menu.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
}