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

    // 실서버 환경변수로 주입되는 프론트 성공 주소 (기본값은 실서버 주소로 안전하게 설정!)
    @Value("${app.frontend.success-url:https://liondelivery.store/oauth/success}")
    private String frontendSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 1. 우리 서비스 전용 JWT Access Token 발급
        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getEmail());
        log.info("🔑 [카카오 로그인 최종 성공] 유저 ID: {}, 토큰 발급 완료!", userDetails.getId());

        // 2. 기본 목적지는 실서버(또는 application.yml에 설정된) 프론트 주소로 설정
        String targetUrlBase = frontendSuccessUrl;

        // 3. 💡 [핵심 스마트 분기 처리] 유저가 로그인을 시도한 원래 위치(Header) 검사!
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");

        // Referer나 Origin 헤더에 'localhost' 또는 '127.0.0.1'이 포함되어 있다면 로컬 테스트로 판단!
        if ((referer != null && (referer.contains("localhost") || referer.contains("127.0.0.1"))) ||
                (origin != null && (origin.contains("localhost") || origin.contains("127.0.0.1")))) {

            // 🔥 프론트엔드 로컬 주소로 목적지 변경! (만약 프론트 팀이 3000 포트를 쓴다면 5173을 3000으로만 바꿔주세요!)
            targetUrlBase = "http://localhost:5173/oauth/success";
            log.info("🏠 [로컬 테스트 감지] 리다이렉트 주소를 Localhost로 변경합니다: {}", targetUrlBase);
        } else {
            log.info("🌐 [실서버 로그인 감지] 리다이렉트 주소를 실서버로 유지합니다: {}", targetUrlBase);
        }

        // 4. 토큰을 파라미터에 예쁘게 붙여서 최종 리다이렉트 URL 생성
        String targetUrl = UriComponentsBuilder.fromUriString(targetUrlBase)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}