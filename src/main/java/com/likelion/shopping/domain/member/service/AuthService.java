package com.likelion.shopping.domain.member.service;

import com.likelion.shopping.domain.member.dto.SignupRequest;
import com.likelion.shopping.domain.member.dto.SignupResponse;
import com.likelion.shopping.domain.member.entity.Member;
import com.likelion.shopping.domain.member.repository.MemberRepository;
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
}