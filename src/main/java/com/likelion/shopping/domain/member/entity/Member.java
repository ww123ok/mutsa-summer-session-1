package com.likelion.shopping.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID; // 💡 [추가 필수] 난수 생성을 위해 임포트!

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

    // DB에 남아있을지 모르는 NOT NULL 제약조건을 완벽히 피해가도록 난수 주입 예정!
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int credit;

    @Column(nullable = false)
    @Builder.Default
    private String role = "ROLE_USER";

    private String provider;     // "kakao"
    private String providerId;   // 카카오 고유 회원번호
    private String profileImage; // 프로필 이미지 URL

    public static Member create(String email, String password, String name, int initialCredit) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .credit(initialCredit)
                .role("ROLE_USER")
                .build();
    }

    // 💡 [핵심 해결 로직] 카카오 소셜 유저 생성 시 DB의 NOT NULL 제약조건을 완벽 방어하기 위해 UUID 난수 비밀번호를 자동 주입!
    public static Member createSocial(String email, String name, String provider, String providerId, String profileImage) {
        return Member.builder()
                .email(email)
                .password(UUID.randomUUID().toString()) // 🔥 여기에 가짜 난수 비밀번호를 무조건 채워 넣습니다!
                .name(name)
                .provider(provider)
                .providerId(providerId)
                .profileImage(profileImage)
                .credit(0)
                .role("ROLE_USER")
                .build();
    }

    public void updateSocialProfile(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
    }

    public void addCredit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.credit += amount;
    }

    public void deductCredit(int amount) {
        if (this.credit < amount) {
            throw new IllegalArgumentException("보유 크레딧이 부족합니다.");
        }
        this.credit -= amount;
    }
}