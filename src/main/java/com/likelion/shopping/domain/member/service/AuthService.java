package com.likelion.shopping.domain.member.service;

import com.likelion.shopping.domain.cart.dto.CartResponse;
import com.likelion.shopping.domain.cart.service.CartService;
import com.likelion.shopping.domain.member.dto.LoginRequest;
import com.likelion.shopping.domain.member.dto.SignupRequest;
import com.likelion.shopping.domain.member.dto.SignupResponse;
import com.likelion.shopping.domain.member.dto.TokenResponse;
import com.likelion.shopping.domain.member.dto.UserResponse;
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
    private final CartService cartService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Member member = Member.create(
                request.getEmail(),
                encodedPassword,
                request.getName(),
                0
        );

        Member savedMember = memberRepository.save(member);

        return SignupResponse.from(savedMember);
    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("가입되지 않은 이메일입니다.")
                );

        if (member.getPassword() == null) {
            throw new IllegalArgumentException(
                    "소셜 로그인으로 가입된 계정입니다."
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                member.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "비밀번호가 일치하지 않습니다."
            );
        }

        String accessToken =
                jwtTokenProvider.createAccessToken(member.getEmail());

        return TokenResponse.of(accessToken);
    }

    public UserResponse getMe(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        CartResponse cartResponse =
                cartService.getCartList(member.getId());

        return UserResponse.of(member, cartResponse);
    }
}