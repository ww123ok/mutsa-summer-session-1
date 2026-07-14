package com.likelion.shopping.domain.member.controller;

import com.likelion.shopping.domain.member.dto.CreditChargeRequest;
import com.likelion.shopping.domain.member.dto.CreditChargeResponse;
import com.likelion.shopping.domain.member.service.MemberService;
import com.likelion.shopping.global.config.CustomUserDetails;
import com.likelion.shopping.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credits")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/charge")
    public ApiResponse<CreditChargeResponse> chargeCredit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreditChargeRequest request) {

        log.info("💰 [크레딧 충전 요청] 로그인 유저 ID: {}, 이메일: {}, 충전 요청 금액: {}원",
                userDetails.getId(), userDetails.getUsername(), request.getAmount());

        CreditChargeResponse response = memberService.chargeCredit(
                userDetails.getId(),
                userDetails.getUsername(),
                request
        );

        return ApiResponse.success(response);
    }
}
