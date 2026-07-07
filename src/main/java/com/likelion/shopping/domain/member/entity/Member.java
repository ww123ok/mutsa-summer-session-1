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

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int credit;

    public static Member create(String email, String password, String name, int initialCredit) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .credit(initialCredit)
                .build();
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
