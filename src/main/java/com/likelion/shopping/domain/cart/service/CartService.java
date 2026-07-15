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
        // 1. 수량 기본 검증
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("장바구니 담기 수량은 1개 이상이어야 합니다.");
        }

        // 2. 회원 및 장바구니 조회 (없으면 생성)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(Cart.create(member)));

        // 3. 담으려는 메뉴 조회
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // 4. 요청으로 들어온 옵션 ID 리스트 정렬 (null이면 빈 리스트로 처리하여 비교 용이하게 함)
        java.util.List<Long> requestOptionIds = (request.getMenuOptionIds() != null)
                ? request.getMenuOptionIds().stream().sorted().toList()
                : java.util.Collections.emptyList();

        // 5. 💡 [중복 방지 핵심 로직] 내 장바구니에 똑같은 메뉴가 이미 있는지 조회!
        java.util.List<CartItem> existingItems = cartItemRepository.findAllByCartIdAndMenuId(cart.getId(), menu.getId());

        for (CartItem existingItem : existingItems) {
            // 기존 아이템이 가지고 있는 옵션 ID 리스트 추출 및 정렬
            java.util.List<Long> existingOptionIds = existingItem.getCartItemOptions().stream()
                    .map(option -> option.getMenuOption().getId())
                    .sorted()
                    .toList();

            // 💡 메뉴가 같고, 선택한 옵션 구성까지 완벽히 일치한다면?!
            if (existingOptionIds.equals(requestOptionIds)) {
                // 새로 만들지 않고 기존 아이템의 수량만 추가 누적 후 종료!
                existingItem.updateQuantity(existingItem.getQuantity() + request.getQuantity());
                return;
            }
        }

        // 6. 일치하는 기존 아이템이 없다면 (아예 새 메뉴거나, 옵션 구성이 다른 경우) 새로 생성!
        CartItem cartItem = CartItem.create(cart, menu, request.getQuantity());
        cartItemRepository.save(cartItem);

        // 7. 옵션 매핑 및 저장
        if (request.getMenuOptionIds() != null && !request.getMenuOptionIds().isEmpty()) {
            for (Long optionId : request.getMenuOptionIds()) {
                MenuOption menuOption = menuOptionRepository.findById(optionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴 옵션입니다."));

                // 타 메뉴 옵션 검증
                if (!menuOption.getMenu().getId().equals(menu.getId())) {
                    throw new IllegalArgumentException("해당 메뉴에 속하지 않는 옵션은 선택할 수 없습니다.");
                }

                CartItemOption cartItemOption = CartItemOption.create(cartItem, menuOption);
                cartItem.addOption(cartItemOption);
                cartItemOptionRepository.save(cartItemOption);
            }
        }
    }

    public CartResponse getCartList(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (cart == null || cart.getCartItems().isEmpty()) {
            Long cartId = (cart != null) ? cart.getId() : null;
            return CartResponse.of(cartId, 0, java.util.Collections.emptyList());
        }

        // 1. 가게별 그룹화
        java.util.Map<com.likelion.shopping.domain.store.entity.Store, java.util.List<CartItem>> groupedItems =
                cart.getCartItems().stream()
                        .collect(java.util.stream.Collectors.groupingBy(item -> item.getMenu().getStore()));

        // 2. 가게별 리스트 변환 및 장바구니 전체 총 금액(Grand Total) 계산
        int grandTotal = 0;
        java.util.List<StoreGroupResponse> storeGroups = new java.util.ArrayList<>();

        for (java.util.Map.Entry<com.likelion.shopping.domain.store.entity.Store, java.util.List<CartItem>> entry : groupedItems.entrySet()) {
            com.likelion.shopping.domain.store.entity.Store store = entry.getKey();

            java.util.List<CartItemResponse> itemResponses = entry.getValue().stream()
                    .map(CartItemResponse::from)
                    .toList();

            // 💡 아이템별 결제 금액을 장바구니 전체 총액에 싹 다 누적 합산!
            for (CartItemResponse itemRes : itemResponses) {
                grandTotal += itemRes.getItemTotalPrice();
            }

            storeGroups.add(StoreGroupResponse.of(store, itemResponses));
        }

        // 3. 최종 반환 (cartId, 전체 총 금액, 가게 목록)
        return CartResponse.of(cart.getId(), grandTotal, storeGroups);
    }

    @Transactional // 💡 [치명적 버그 해결] readOnly = true를 덮어쓰기 위해 반드시 필요!
    public void updateCartItemQuantity(Long memberId, Long cartItemId, CartItemQuantityRequest request) {
        // 💡 [피드백 반영] 수량 0 이하로 변경 시도 시 예외 처리
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("장바구니 상품 수량은 1개 이상이어야 합니다.");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 상품입니다."));

        if (!cartItem.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 상품의 수량을 변경할 권한이 없습니다.");
        }

        cartItem.updateQuantity(request.getQuantity());
    }

    @Transactional // 💡 [치명적 버그 해결] 삭제가 DB에 반영되도록 반드시 필요!
    public void deleteCartItem(Long memberId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 상품입니다."));

        if (!cartItem.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 상품을 삭제할 권한이 없습니다.");
        }

        cartItemRepository.delete(cartItem);
    }
}