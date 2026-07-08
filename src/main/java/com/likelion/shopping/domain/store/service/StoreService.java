package com.likelion.shopping.domain.store.service;

import com.likelion.shopping.domain.menu.entity.Menu;
import com.likelion.shopping.domain.menu.repository.MenuRepository;
import com.likelion.shopping.domain.store.dto.StoreDetailResponse;
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
    private final MenuRepository menuRepository;

    /**
     * 가게 목록 조회 로직 (카테고리 필터링 포함)
     */
    public List<StoreResponse> getStores(String category) {
        // 1. 조건별 데이터 엔티티 조회: 카테고리 파라미터 유무에 따른 분기 처리 및 데이터 추출
        List<Store> stores = (category == null || category.isBlank())
                ? storeRepository.findAll()
                : storeRepository.findByCategory(category);

        // 2. DTO 변환 및 반환: Stream API를 활용하여 조회된 엔티티 리스트를 응답 DTO 포맷으로 매핑
        return stores.stream()
                .map(StoreResponse::from)
                .toList();
    }

    /**
     * 가게 상세 및 메뉴 목록 조회 로직
     */
    public StoreDetailResponse getStoreDetail(Long storeId) {
        // 1. 단일 엔티티 검증 및 조회: ID 기반 조회 수행 및 예외 처리 구현
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다. ID: " + storeId));

        // 2. 연관 데이터 엔티티 조회: 해당 가게 ID와 연관관계 맺은 메뉴 엔티티 리스트 추출
        List<Menu> menus = menuRepository.findByStoreId(storeId);

        // 3. 데이터 결합 및 DTO 변환: 조회된 복합 엔티티 객체들을 정적 팩토리 메서드를 통해 최종 응답 DTO 객체로 조립 후 반환
        return StoreDetailResponse.of(store, menus);
    }
}
