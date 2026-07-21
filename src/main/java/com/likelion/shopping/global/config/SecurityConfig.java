package com.likelion.shopping.global.config;

import com.likelion.shopping.global.jwt.JwtAuthenticationFilter;
import com.likelion.shopping.global.jwt.JwtTokenProvider;
import com.likelion.shopping.global.security.CustomAccessDeniedHandler;
import com.likelion.shopping.global.security.CustomAuthenticationEntryPoint;
import com.likelion.shopping.global.security.CustomOAuth2UserService;
import com.likelion.shopping.global.security.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    // 카카오 소셜 로그인 전용 빈 주입
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final String[] allowUris = {
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/api/auth/**",
            "/oauth2/**",       // 카카오 로그인 시작점 허용
            "/login/oauth2/**", // 카카오 리다이렉트 콜백 허용
            "/error",           // 스프링 부트 기본 에러 페이지 허용 (추가!)
            "/favicon.ico"      // 파비콘 에러 방지 (추가!)
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 💡 [수정 1] CORS 빈이 정확하게 매핑되도록 깔끔하게 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 💡 [핵심] URL 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allowUris).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stores/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 💡 [핵심] 카카오 소셜 로그인 기능 연결 (리다이렉트 및 성공 핸들러)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )

                // 💡 [수정 2] JWT 필터를 거칠 때 allowUris는 안전하게 통과되도록 배치
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                )

                // 💡 [수정 3] 에러 핸들링을 맨 뒤로 배치하여 정상적인 OAuth2 리다이렉트를 가로채지 않도록 방어!
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://www.liondelivery.store",
                "https://liondelivery.store"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}