package com.likelion.shopping.domain.menu.repository;

import com.likelion.shopping.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    // storeId 조회 시, 해당 가게에 속한 메뉴를 모두 불러오는 메서드
    List<Menu> findByStoreId(Long storeId);
}
