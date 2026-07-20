package com.likelion.shopping.global.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Long id;
    private final String email;
    private final String role; // 회원의 권한 등급 저장
    private Map<String, Object> attributes; // 카카오 원본 데이터

    // 일반 로그인 시 사용하는 생성자
    public CustomUserDetails(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // 카카오 소셜 로그인 시 사용하는 생성자
    public CustomUserDetails(Long id, String email, String role, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.attributes = attributes;
    }

    // [OAuth2User 인터페이스 구현]
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    // [UserDetails 인터페이스 구현]
    @Override
    public String getUsername() {
        return this.email;
    }

    // 실제 회원이 가진 권한(ROLE_USER 등)을 스프링 시큐리티에 보고!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null || this.role.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() { return null; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}