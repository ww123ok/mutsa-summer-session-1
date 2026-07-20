package com.likelion.shopping.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 소셜 로그인 유저는 비밀번호가 없으므로 nullable = false 제거
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int credit;

    // [실무 RBAC 적용] 회원의 권한 등급을 저장 (기본값: "ROLE_USER") - 이거 실무 스타일로 한 번 적용해봤음
    @Column(nullable = false)
    @Builder.Default
    private String role = "ROLE_USER";

    // 소셜 로그인 확장 필드
    private String provider;     // "kakao"
    private String providerId;   // 카카오 고유 회원번호
    private String profileImage; // 프로필 이미지 URL

    // 일반 회원가입용 정적 팩토리 메서드
    public static Member create(String email, String password, String name, int initialCredit) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .credit(initialCredit)
                .role("ROLE_USER") // 일반 가입 시 기본 유저 권한 부여
                .build();
    }

    // 카카오 소셜 유저 최초 가입용 정적 팩토리 메서드
    public static Member createSocial(String email, String name, String provider, String providerId, String profileImage) {
        return Member.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .providerId(providerId)
                .profileImage(profileImage)
                .credit(0)
                .role("ROLE_USER") // 소셜 가입 시 기본 유저 권한 부여
                .build();
    }

    // 카카오 프로필(닉네임/프사) 변경 시 DB를 업데이트하는 비즈니스 메서드
    public void updateSocialProfile(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
    }

    // 크레딧 충전 로직
    public void addCredit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.credit += amount;
    }

    // 크레딧 차감 로직
    public void deductCredit(int amount) {
        if (this.credit < amount) {
            throw new IllegalArgumentException("보유 크레딧이 부족합니다.");
        }
        this.credit -= amount;
    }
}