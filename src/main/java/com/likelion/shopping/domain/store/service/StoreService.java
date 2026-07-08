package com.likelion.shopping.domain.store.service;

import com.likelion.shopping.domain.store.dto.StoreResponse;
import com.likelion.shopping.domain.store.entity.Store;
import com.likelion.shopping.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    /**
     * 가게 목록 조회 로직 (카테고리 필터링 포함)
     */
    public List<StoreResponse> getStores(String category) {
        // 1. 카테고리 파라미터 유무에 따라 분기 처리하여 DB에서 엔티티 리스트 조회
        List<Store> stores = (category == null || category.isBlank())
                ? storeRepository.findAll()
                : storeRepository.findByCategory(category);

        // 2. 스트림을 활용하여 조회된 엔티티 리스트를 DTO 리스트로 변환 후 반환
        return stores.stream()
                .map(StoreResponse::from)
                .toList();
    }
}
