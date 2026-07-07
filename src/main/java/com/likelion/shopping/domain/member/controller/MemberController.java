package com.likelion.shopping.domain.member.controller;

import com.likelion.shopping.domain.member.dto.CreditChargeRequest;
import com.likelion.shopping.domain.member.dto.CreditChargeResponse;
import com.likelion.shopping.domain.member.service.MemberService;
import com.likelion.shopping.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credits")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/charge")
    public ApiResponse<CreditChargeResponse> chargeCredit(
            @RequestHeader("Member-Id") Long memberId,
            @RequestBody CreditChargeRequest request) {

        CreditChargeResponse response = memberService.chargeCredit(memberId, request);

        return ApiResponse.success(response);
    }
}
