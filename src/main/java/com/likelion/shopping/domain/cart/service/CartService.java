package com.likelion.shopping.domain.cart.service;

import com.likelion.shopping.domain.cart.dto.CartItemRequest;
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
}