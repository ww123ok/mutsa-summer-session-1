package com.likelion.shopping.global.security;

import com.likelion.shopping.global.config.CustomUserDetails;
import com.likelion.shopping.global.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    // 환경변수로 프론트 성공 주소 주입 (기본값 로컬 5173)
    @Value("${app.frontend.success-url:http://localhost:5173/oauth/success}")
    private String frontendSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 우리 서비스 전용 JWT Access Token 발급
        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getEmail());
        log.info("🔑 [카카오 로그인 최종 성공] 유저 ID: {}, 토큰 발급 완료!", userDetails.getId());

        String targetUrl = UriComponentsBuilder.fromUriString(frontendSuccessUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}