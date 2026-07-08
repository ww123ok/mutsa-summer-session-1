package com.likelion.shopping.domain.cart.service;

import com.likelion.shopping.domain.cart.dto.*;
import com.likelion.shopping.domain.cart.entity.Cart;
import com.likelion.shopping.domain.cart.entity.CartItem;
import com.likelion.shopping.domain.cart.entity.CartItemOption;
import com.likelion.shopping.domain.cart.repository.CartItemOptionRepository;
import com.likelion.shopping.domain.cart.repository.CartItemRepository;
import com.likelion.shopping.domain.cart.repository.CartRepository;
import com.likelion.shopping.domain.member.entity.Member;
import com.likelion.shopping.domain.member.repository.MemberRepository;
import com.likelion.shopping.domain.menu.entity.Menu;
import com.likelion.shopping.domain.menu.entity.MenuOption;
import com.likelion.shopping.domain.menu.repository.MenuOptionRepository;
import com.likelion.shopping.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final MemberRepository memberRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;

    @Transactional
    public void addCartItem(Long memberId, CartItemRequest request) {
        // 1. 회원 조회 (회원 테이블에서 직접 찾아옴)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 회원의 장바구니 찾기 (없으면 새로 생성)
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(Cart.create(member)));

        // 3. 담으려는 메뉴 조회
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // 4. CartItem 생성 및 저장
        CartItem cartItem = CartItem.create(cart, menu, request.getQuantity());
        cartItemRepository.save(cartItem);

        // 5. 선택한 옵션들이 있다면 매핑해서 저장
        if (request.getMenuOptionIds() != null) {
            for (Long optionId : request.getMenuOptionIds()) {
                MenuOption menuOption = menuOptionRepository.findById(optionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴 옵션입니다."));

                CartItemOption cartItemOption = CartItemOption.create(cartItem, menuOption);
                cartItemOptionRepository.save(cartItemOption);
            }
        }
    }

    public CartResponse getCartList(Long memberId) {
        // 1. 회원의 장바구니 조회 (없으면 빈 장바구니 반환)
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (cart == null || cart.getCartItems().isEmpty()) {
            return CartResponse.from(java.util.Collections.emptyList());
        }

        // 2. 장바구니 아이템들을 '가게(Store)' 기준으로 그룹화 (Map<Store, List<CartItem>>)
        java.util.Map<com.likelion.shopping.domain.store.entity.Store, java.util.List<CartItem>> groupedItems =
                cart.getCartItems().stream()
                        .collect(java.util.stream.Collectors.groupingBy(item -> item.getMenu().getStore()));

        // 3. 그룹화된 데이터를 DTO로 변환
        java.util.List<StoreGroupResponse> storeGroups = groupedItems.entrySet().stream()
                .map(entry -> {
                    com.likelion.shopping.domain.store.entity.Store store = entry.getKey();
                    java.util.List<CartItemResponse> itemResponses = entry.getValue().stream()
                            .map(CartItemResponse::from)
                            .toList();
                    return StoreGroupResponse.of(store, itemResponses);
                })
                .toList();

        // 4. 최종 결과 반환
        return CartResponse.from(storeGroups);
    }



    public void updateCartItemQuantity(Long memberId, Long cartItemId, CartItemQuantityRequest request) {
        // 1. 변경할 장바구니 아이템 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 상품입니다."));

        // 2. 이 장바구니 상품이 내 것인지 권한 체크 (보안 필수!)
        if (!cartItem.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 상품의 수량을 변경할 권한이 없습니다.");
        }

        // 3. 수량 변경 (JPA 변경 감지로 인해 알아서 DB에 UPDATE 쿼리가 날아감)
        cartItem.updateQuantity(request.getQuantity());
    }

    public void deleteCartItem(Long memberId, Long cartItemId) {
        // 1. 삭제할 장바구니 아이템 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 상품입니다."));

        // 2. 이 장바구니 상품이 내 것인지 권한 체크 (보안 필수!)
        if (!cartItem.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 상품을 삭제할 권한이 없습니다.");
        }

        // 3. 상품 삭제
        cartItemRepository.delete(cartItem);
    }
}