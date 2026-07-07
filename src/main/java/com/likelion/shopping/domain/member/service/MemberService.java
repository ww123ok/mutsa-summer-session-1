package com.likelion.shopping.domain.member.service;

import com.likelion.shopping.domain.member.dto.CreditChargeRequest;
import com.likelion.shopping.domain.member.dto.CreditChargeResponse;
import com.likelion.shopping.domain.member.entity.Member;
import com.likelion.shopping.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public CreditChargeResponse chargeCredit(Long memberId, CreditChargeRequest request) {

        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.addCredit(request.getAmount());

        return new CreditChargeResponse(member.getCredit());
    }
}
