package com.likelion.shopping.domain.member.service;

import com.likelion.shopping.domain.member.dto.LoginRequest;
import com.likelion.shopping.domain.member.dto.SignupRequest;
import com.likelion.shopping.domain.member.dto.SignupResponse;
import com.likelion.shopping.domain.member.dto.TokenResponse;
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

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 이메일 중복 검증
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2. 비밀번호 암호화 (평문 저장 절대 금지)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 팀에서 만든 정적 팩토리 메서드(Member.create)를 사용하여 회원 생성
        //    (가입 축하금으로 초기 크레딧은 0으로 설정, 필요시 수정 가능)
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

    // 2. 로그인 및 JWT 토큰 발급 메서드 추가!
    public TokenResponse login(LoginRequest request) {
        // ① 이메일로 DB에서 회원 조회 (없으면 에러 발생)
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // ② 비밀번호 일치 여부 검사! (입력한 평문 비밀번호 vs DB에 저장된 암호화 비밀번호 비교)
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // ③ 검증 완료! 토큰 기계로 Access Token 발급
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());

        // ④ TokenResponse DTO에 담아서 반환
        return TokenResponse.of(accessToken);
    }
}