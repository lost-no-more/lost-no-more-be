package org.lostnomore.backend.auth.provider;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.dto.response.AccessTokenDto;
import org.lostnomore.backend.auth.dto.response.KakaoUserDto;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class KakaoProvider implements OAuthProvider{

    @Value("${oauth2.kakao.token-url}")
    private String KAKAO_ACCESS_TOKEN_URL;

    @Value("${oauth2.kakao.user-info-url}")
    private String KAKAO_USER_INFO_URL;

    @Value("${oauth2.kakao.code-url}")
    private String KAKAO_CODE_URL;

    @Value("${oauth2.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${oauth2.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${oauth2.kakao.redirect-url}")
    private String KAKAO_LOGIN_REDIRECT_URL;

    @Value("${oauth2.kakao.scope}")
    private String KAKAO_SCOPE;

    private final RestTemplate restTemplate;

    @Override
    public String getCodeUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s",
                KAKAO_CODE_URL,
                URLEncoder.encode(KAKAO_CLIENT_ID, StandardCharsets.UTF_8),
                URLEncoder.encode(KAKAO_LOGIN_REDIRECT_URL, StandardCharsets.UTF_8),
                URLEncoder.encode(KAKAO_SCOPE, StandardCharsets.UTF_8));
    }

    @Override
    public String getAccessToken(String code) {
        try {
            final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("client_secret", KAKAO_CLIENT_SECRET);
            params.add("redirect_uri", KAKAO_LOGIN_REDIRECT_URL);
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<AccessTokenDto> response = restTemplate.postForEntity(
                    KAKAO_ACCESS_TOKEN_URL,
                    request,
                    AccessTokenDto.class);

            return Objects.requireNonNull(response.getBody()).getAccessToken();
        } catch (HttpClientErrorException e) {
            throw new BusinessException(AuthErrorCode.INVALID_CODE);
        } catch (HttpServerErrorException | NullPointerException e) {
            throw new BusinessException(AuthErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getUserInfo(String accessToken) {
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(accessToken);

            final HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<KakaoUserDto> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    KakaoUserDto.class
            );
            return Objects.requireNonNull(response.getBody()).getKakaoAccount().email();
        } catch (HttpClientErrorException e) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (HttpServerErrorException | NullPointerException e) {
            throw new BusinessException(AuthErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
