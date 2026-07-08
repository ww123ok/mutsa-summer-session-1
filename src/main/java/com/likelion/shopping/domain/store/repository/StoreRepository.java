package com.likelion.shopping.domain.store.repository;

import com.likelion.shopping.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 카테고리가 파라미터로 들어오면 필터링하여 리스트를 찾아주는 메서드
    List<Store> findByCategory(String category);
}
