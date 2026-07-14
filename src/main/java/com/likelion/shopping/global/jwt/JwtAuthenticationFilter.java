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

        // 1. 요청 헤더에서 Authorization 추출
        String header = request.getHeader("Authorization");

        // 2. Bearer 토큰이 있는지 확인
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // 3. 토큰이 유효한지 검증 (아까 만든 validateToken 사용)
                if (jwtTokenProvider.validateToken(token)) {
                    // 4. 유효하면 이메일(사용자 정보) 가져오기
                    String email = jwtTokenProvider.getEmail(token);

                    // 5. DB에서 상세 정보 로드
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    // 6. 인증 객체 생성 및 SecurityContext 등록
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 토큰이 만료되었거나 위조된 경우, 인증 정보를 안전하게 초기화
                SecurityContextHolder.clearContext();
            }
        }

        // 7. 다음 필터로 무조건 넘겨줌
        filterChain.doFilter(request, response);
    }
}
