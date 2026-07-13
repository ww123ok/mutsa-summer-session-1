package com.likelion.shopping.global.jwt;

import com.likelion.shopping.global.config.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        try {
            // 1. 헤더에 Bearer 토큰이 제대로 담겨왔는지 확인
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                // 2. 토큰이 유효한지 검증
                if (jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getEmail(token);

                    // 3. 유저 상세 정보 DB에서 로드
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    // 4. 인증 객체(Authentication) 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 5. 시큐리티 저장소에 인증 유저로 등록
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // 토큰 검증이나 유저 조회 중 문제가 생기면 인증 정보 삭제
            SecurityContextHolder.clearContext();
        }

        // 6. 다음 필터로 넘김 -> SecurityConfig가 401 에러 냄
        filterChain.doFilter(request, response);
    }
}
