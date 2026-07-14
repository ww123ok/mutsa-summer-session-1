package com.likelion.shopping.domain.member.service;

import com.likelion.shopping.domain.member.dto.CreditChargeRequest;
import com.likelion.shopping.domain.member.dto.CreditChargeResponse;
import com.likelion.shopping.domain.member.entity.Member;
import com.likelion.shopping.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public CreditChargeResponse chargeCredit(Long memberId, String email, CreditChargeRequest request) {
        // [유효성 검사] 0원 이하 및 마이너스 금액 충전 방지
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("충전 금액은 1원 이상이어야 합니다.");
        }

        // [쏠림 방지 핵심 로직] Email로 먼저 조회하고, 없을 때만 ID로 조회하여
        // JWT 필터의 ID 파싱 오류나 하드코딩으로 인한 '한 사람에게 돈 몰림 현상'을 차단
        Member member = null;
        if (email != null && !email.isEmpty()) {
            member = memberRepository.findByEmail(email).orElse(null);
        }
        if (member == null && memberId != null) {
            member = memberRepository.findByIdWithLock(memberId)
                    .orElseGet(() -> memberRepository.findById(memberId).orElse(null));
        }

        // 그래도 회원을 찾지 못했다면 예외 처리
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않거나 로그인 정보가 잘못된 회원입니다.");
        }

        // 진짜 주인의 지갑에 크레딧 누적 및 완료 로그 출력
        int beforeCredit = member.getCredit();
        member.addCredit(request.getAmount());
        int afterCredit = member.getCredit();

        log.info("✅ [충전 완료] 회원({}): 기존 {}원 ➔ 변경 {}원 (+{}원 충전)",
                member.getEmail(), beforeCredit, afterCredit, request.getAmount());

        return new CreditChargeResponse(afterCredit);
    }
}
