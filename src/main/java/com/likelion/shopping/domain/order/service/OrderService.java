package com.likelion.shopping.domain.order.service;

import com.likelion.shopping.domain.cart.entity.Cart;
import com.likelion.shopping.domain.cart.entity.CartItem;
import com.likelion.shopping.domain.cart.entity.CartItemOption;
import com.likelion.shopping.domain.cart.repository.CartRepository;
import com.likelion.shopping.domain.member.entity.Member;
import com.likelion.shopping.domain.member.repository.MemberRepository;
import com.likelion.shopping.domain.order.dto.OrderCreateResponse;
import com.likelion.shopping.domain.order.entity.OrderItem;
import com.likelion.shopping.domain.order.entity.OrderItemOption;
import com.likelion.shopping.domain.order.entity.Orders;
import com.likelion.shopping.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderCreateResponse checkoutOrder(Long memberId) {

        // 데이터베이스에서 회원 지갑 정보와 해당 회원의 장바구니 정보를 조회
        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));

        // 장바구니가 비어있다면 결제를 진행할 수 없으므로 방어벽 치기
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있어 주문을 진행할 수 없습니다.");
        }

        // 장바구니 안에 담긴 모든 상품들과 선택 옵션들의 가격을 취합하여 총 결제 금액 계산
        int totalPrice = 0;
        for (CartItem cartItem : cart.getCartItems()) {
            int itemPrice = cartItem.getMenu().getPrice();

            for (CartItemOption option : cartItem.getCartItemOptions()) {
                itemPrice += option.getMenuOption().getAdditionalPrice();
            }

            totalPrice += (itemPrice * cartItem.getQuantity());
        }

        // 회원 엔티티 스스로 잔액을 검증하고 차감하도록 시킴(만약 보유 금액이 부족하면 deductCredit 내부에서 IllegalArgumentException이 터짐
        member.deductCredit(totalPrice);

        // 확정된 금액을 바탕으로 진짜 주문 영수증(Orders) 부모 객체 먼저 생성
        Orders orders = Orders.create(member, totalPrice);

        // 장바구니 항목들을 하나씩 순회하며 과거 결제 영수증 스냅샷(OrderItem)으로 변환 박제
        for (CartItem cartItem : cart.getCartItems()) {

            int singleItemPrice = cartItem.getMenu().getPrice();
            for (CartItemOption option : cartItem.getCartItemOptions()) {
                singleItemPrice += option.getMenuOption().getAdditionalPrice();
            }

            OrderItem orderItem = OrderItem.create(
                    orders,
                    cartItem.getMenu().getName(),
                    singleItemPrice,
                    cartItem.getQuantity()
            );

            // 해당 상품에 붙어있던 옵션들도 영수증 옵션 스냅샷으로 복사 박제
            for (CartItemOption cartOption : cartItem.getCartItemOptions()) {
                OrderItemOption orderOption = OrderItemOption.create(
                        orderItem,
                        cartOption.getMenuOption().getName(),
                        cartOption.getMenuOption().getAdditionalPrice()
                );
                orderItem.addOption(orderOption);
            }

            // 완성된 자식 영수증 항목을 부모 영수증(Orders) 리스트에 추가
            orders.addOrderItem(orderItem);
        }

        // cascade = CascadeType.ALL 설정으로 부모 영수증 하나만 저장해도 하위의 모든 스냅샷 항모과 옵션들이 DB에 한 번에 깨끗하게 저장됨
        orderRepository.save(orders);

        // 결제 성공했으므로 사용자의 장바구니 비우기. orphanRemoval = true 설정으로 자바 리스트를 비우면 DB의 장바구니 데이터도 자동 삭제됨
        cart.clearCart();

        // API 명세서 규격에 맞춰 컨트롤러로 리턴
        return new OrderCreateResponse(orders.getId(), totalPrice, member.getCredit());
    }
}
