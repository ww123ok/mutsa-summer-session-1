package com.likelion.shopping.domain.store.controller;

import com.likelion.shopping.domain.store.dto.StoreDetailResponse;
import com.likelion.shopping.domain.store.dto.StoreResponse;
import com.likelion.shopping.domain.store.service.StoreService;
import com.likelion.shopping.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    /**
     * 전체 가게 목록 조회 API (카테고리 필터링 포함)
     */
    @GetMapping
    public ApiResponse<List<StoreResponse>> getStores(
            @RequestParam(value = "category", required = false) String category) {

        List<StoreResponse> response = storeService.getStores(category);
        return ApiResponse.success(200, "가게 목록 조회 성공", response);
    }

    /**
     * 특정 가게 상세 및 메뉴 목록 조회 API
     */
    @GetMapping("/{storeId}")
    public ApiResponse<StoreDetailResponse> getStoreDetail(@PathVariable("storeId") Long storeId) {

        StoreDetailResponse response = storeService.getStoreDetail(storeId);

        return ApiResponse.success(200, "가게 상세 조회 성공", response);
    }
}
