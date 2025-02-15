package org.lostnomore.backend.auth.provider;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.dto.UserInfoDto;
import org.lostnomore.backend.auth.dto.response.AccessTokenDto;
import org.lostnomore.backend.auth.dto.response.GoogleUserDto;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.lostnomore.backend.global.exception.code.BusinessErrorCode;
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
public class GoogleProvider implements OAuthProvider {

    @Value("${oauth2.google.token-url}")
    private String GOOGLE_ACCESS_TOKEN_URL;

    @Value("${oauth2.google.code-url}")
    private String GOOGLE_CODE_URL;

    @Value("${oauth2.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${oauth2.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${oauth2.google.redirect-url}")
    private String GOOGLE_LOGIN_REDIRECT_URL;

    @Value("${oauth2.google.user-info-url}")
    private String GOOGLE_USER_INFO_URL;

    @Value("${oauth2.google.unlink-url}")
    private String GOOGLE_UNLINK_URL;

    @Value("${oauth2.google.scope}")
    private String GOOGLE_SCOPE;

    private final RestTemplate restTemplate;

    @Override
    public String getCodeUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s",
                GOOGLE_CODE_URL,
                URLEncoder.encode(GOOGLE_CLIENT_ID, StandardCharsets.UTF_8),
                URLEncoder.encode(GOOGLE_LOGIN_REDIRECT_URL, StandardCharsets.UTF_8),
                URLEncoder.encode(GOOGLE_SCOPE, StandardCharsets.UTF_8));
    }

    @Override
    public String getAccessToken(String code) {
        try {
            final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", GOOGLE_CLIENT_ID);
            params.add("client_secret", GOOGLE_CLIENT_SECRET);
            params.add("redirect_uri", GOOGLE_LOGIN_REDIRECT_URL);
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<AccessTokenDto> response = restTemplate.postForEntity(
                    GOOGLE_ACCESS_TOKEN_URL,
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
    public void unLink(String providerId, String code) {
        try {
            if (code == null) {
                throw new BusinessException(BusinessErrorCode.MISSING_REQUIRED_PARAMETER);
            }
            String accessToken = getAccessToken(code);
            final MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("token", accessToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            final HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

            restTemplate.postForLocation(GOOGLE_UNLINK_URL, request);
        } catch (HttpClientErrorException e) {
            throw new BusinessException(AuthErrorCode.INVALID_CODE);
        } catch (HttpServerErrorException | NullPointerException e) {
            throw new BusinessException(AuthErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserInfoDto getUserInfo(String accessToken) {
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(accessToken);

            final HttpEntity<Object> request = new HttpEntity<>(headers);

            ResponseEntity<GoogleUserDto> response = restTemplate.exchange(
                    GOOGLE_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    GoogleUserDto.class
            );

            GoogleUserDto googleUserDto = Objects.requireNonNull(response.getBody());
            return new UserInfoDto(googleUserDto.getId(), googleUserDto.getEmail());
        } catch (HttpClientErrorException e) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (HttpServerErrorException | NullPointerException e) {
            throw new BusinessException(AuthErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}