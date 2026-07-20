package com.likelion.shopping.global.security;

import com.likelion.shopping.domain.member.entity.Member;
import com.likelion.shopping.domain.member.repository.MemberRepository;
import com.likelion.shopping.global.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "kakao"
        String providerId = String.valueOf(attributes.get("id"));

        Map<String, Object> properties = (Map<String, Object>) attributes.getOrDefault("properties", Map.of());
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.getOrDefault("kakao_account", Map.of());

        String nickname = (String) properties.get("nickname");
        String profileImage = (String) properties.get("profile_image");
        String email = (String) kakaoAccount.get("email");

        // 이메일 권한이 없거나 null이면 고유 회원번호로 안전하게 가짜 이메일 생성
        if (email == null || email.isBlank()) {
            email = providerId + "@kakao.social";
        }

        log.info("💛 [카카오 로그인 시도] providerId: {}, nickname: {}, email: {}", providerId, nickname, email);

        String finalEmail = email;
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .map(existingMember -> {
                    existingMember.updateSocialProfile(nickname, profileImage);
                    return existingMember;
                })
                .orElseGet(() -> {
                    log.info("🎉 [카카오 신규 가입] 신규 소셜 유저를 DB에 등록합니다.");
                    Member newMember = Member.createSocial(finalEmail, nickname, provider, providerId, profileImage);
                    return memberRepository.save(newMember);
                });

        // [RBAC 적용 완료] member.getRole()을 넘겨주어 권한까지 완벽 부여
        return new CustomUserDetails(member.getId(), member.getEmail(), member.getRole(), attributes);
    }
}