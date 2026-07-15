package com.likelion.shopping.domain.member.service;

import com.likelion.shopping.domain.cart.dto.CartResponse;
import com.likelion.shopping.domain.cart.service.CartService;
import com.likelion.shopping.domain.member.dto.*;
import com.likelion.shopping.domain.member.entity.Member;
import com.likelion.shopping.domain.member.repository.MemberRepository;
import com.likelion.shopping.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 장바구니 목록 조회를 위해 CartService 주입
    private final CartService cartService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 이메일 중복 검증
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2. 비밀번호 암호화 (평문 저장 절대 금지)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 팀에서 만든 정적 팩토리 메서드(Member.create)를 사용하여 회원 생성
        Member member = Member.create(
                request.getEmail(),
                encodedPassword,
                request.getName(),
                0
        );

        // 4. DB에 저장
        Member savedMember = memberRepository.save(member);

        // 5. Response DTO로 변환하여 반환
        return SignupResponse.from(savedMember);
    }

    // 로그인 및 JWT 토큰 발급 메서드
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());

        return TokenResponse.of(accessToken);
    }

    // 내 프로필 조회 시 장바구니 목록까지 한 번에 싹 긁어서 합체 반환!
    public UserResponse getMe(String email) {
        // 1. 이메일로 내 회원 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. CartService의 getCartList를 호출해 내 장바구니(가게, 메뉴, 옵션, 수량) 전체 조회!
        CartResponse cartResponse = cartService.getCartList(member.getId());

        // 3. 프로필 + 장바구니를 합체해서 최종 반환!
        return UserResponse.of(member, cartResponse);
    }
}