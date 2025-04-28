package org.lostnomore.backend.auth.oauth.kakao;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.oauth.kakao.dto.KakaoTokenResponse;
import org.lostnomore.backend.auth.oauth.kakao.dto.KakaoUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoProvider {

    private static final String GRANT_TYPE = "authorization_code";
    private static final String Bearer = "Bearer ";
    private static final String KAKAO_ADMIN_KEY = "KakaoAK ";
    private static final String KAKAO_TARGET_TYPE = "user_id";



    @Value("${oauth2.kakao.code-url}")
    private String codeUrl;

    @Value("${oauth2.kakao.client-id}")
    private String clientId;

    @Value("${oauth2.kakao.client-secret}")
    private String clientSecret;

    @Value("${oauth2.kakao.admin-key}")
    private String adminKey;

    @Value("${oauth2.kakao.redirect-url}")
    private String redirectUrl;

    @Value("${oauth2.kakao.scope}")
    private String scope;

    private final KakaoApiClient kakaoApiClient;
    private final KakaoAuthApiClient kakaoAuthApiClient;

    public String getCodeUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s",
                codeUrl,
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8),
                URLEncoder.encode(scope, StandardCharsets.UTF_8));
    }

    public KakaoUserResponse getUserInfo(String code) {
        KakaoTokenResponse tokenResponse = kakaoAuthApiClient.getOAuth2AccessToken(
                GRANT_TYPE,
                clientId,
                clientSecret,
                redirectUrl,
                code
        );

        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation(Bearer + tokenResponse.accessToken());

        return userResponse;
    }

    public void unLink(Long providerId) {
        kakaoApiClient.withdraw(KAKAO_ADMIN_KEY + adminKey,
                KAKAO_TARGET_TYPE,
                providerId);
    }
}