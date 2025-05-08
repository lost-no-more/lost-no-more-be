package org.lostnomore.backend.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.auth.oauth.dto.UserTokenResponse;
import org.lostnomore.backend.auth.oauth.kakao.KakaoProvider;
import org.lostnomore.backend.auth.oauth.kakao.dto.KakaoAccount;
import org.lostnomore.backend.auth.oauth.kakao.dto.KakaoUserResponse;
import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserCreator;
import org.lostnomore.backend.user.manager.UserRemover;
import org.lostnomore.backend.user.manager.UserRetriever;
import org.lostnomore.backend.user.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KakaoProvider kakaoProvider;

    @Mock
    private UserService userService;

    @Mock
    private UserRetriever userRetriever;

    @Mock
    private UserCreator userCreator;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRemover userRemover;

    @InjectMocks
    private AuthService authService;

    private final String TEST_CODE = "test_authorization_code";
    private final Long TEST_PROVIDER_ID = 1L;
    private final Long TEST_USER_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_ACCESS_TOKEN = "test_access_token";
    private final String TEST_REFRESH_TOKEN = "test_refresh_token";

    @Test
    @DisplayName("새로운 사용자 OAuth 로그인 테스트")
    void oauthLogin_NewUser_ShouldRegisterAndReturnToken() {
        User testUser = mock(User.class);
        when(testUser.getId()).thenReturn(TEST_USER_ID);

        KakaoAccount kakaoAccount = new KakaoAccount(TEST_EMAIL);
        KakaoUserResponse kakaoUserResponse = new KakaoUserResponse(TEST_PROVIDER_ID, kakaoAccount);

        when(kakaoProvider.getUserInfo(TEST_CODE)).thenReturn(kakaoUserResponse);
        when(userRetriever.findByProviderId(TEST_PROVIDER_ID)).thenReturn(null);
        when(userService.register(TEST_PROVIDER_ID, TEST_EMAIL)).thenReturn(testUser);
        when(jwtTokenProvider.createLoginToken(String.valueOf(TEST_USER_ID)))
                .thenReturn(new UserTokenResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN));

        UserTokenResponse response = authService.oauthLogin(TEST_CODE);

        verify(kakaoProvider).getUserInfo(TEST_CODE);
        verify(userRetriever).findByProviderId(TEST_PROVIDER_ID);
        verify(userService).register(TEST_PROVIDER_ID, TEST_EMAIL);
        verify(userCreator).save(testUser);
        verify(jwtTokenProvider).createLoginToken(String.valueOf(TEST_USER_ID));

        assertNotNull(response);
        assertEquals(TEST_ACCESS_TOKEN, response.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.refreshToken());
    }

    @Test
    @DisplayName("기존 사용자 OAuth 로그인 테스트")
    void oauthLogin_ExistingUser_ShouldReturnToken() {
        User existingUser = mock(User.class);
        when(existingUser.getId()).thenReturn(TEST_USER_ID);

        KakaoAccount kakaoAccount = new KakaoAccount(TEST_EMAIL);
        KakaoUserResponse kakaoUserResponse = new KakaoUserResponse(TEST_PROVIDER_ID, kakaoAccount);

        when(kakaoProvider.getUserInfo(TEST_CODE)).thenReturn(kakaoUserResponse);
        when(userRetriever.findByProviderId(TEST_PROVIDER_ID)).thenReturn(existingUser);
        when(jwtTokenProvider.createLoginToken(String.valueOf(TEST_USER_ID)))
                .thenReturn(new UserTokenResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN));

        UserTokenResponse response = authService.oauthLogin(TEST_CODE);

        verify(kakaoProvider).getUserInfo(TEST_CODE);
        verify(userRetriever).findByProviderId(TEST_PROVIDER_ID);
        verify(userService, never()).register(anyLong(), anyString());
        verify(userCreator, never()).save(any(User.class));
        verify(jwtTokenProvider).createLoginToken(String.valueOf(TEST_USER_ID));

        assertNotNull(response);
        assertEquals(TEST_ACCESS_TOKEN, response.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.refreshToken());
    }

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    void reissue_ValidToken_ShouldReturnNewTokens() {
        String userId = String.valueOf(TEST_USER_ID);

        doNothing().when(jwtTokenProvider).validateToken(TEST_REFRESH_TOKEN);
        when(jwtTokenProvider.getUserIdOnToken(TEST_REFRESH_TOKEN)).thenReturn(userId);
        when(jwtTokenProvider.compareRefreshToken(userId, TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.regenerateAccessToken(userId)).thenReturn("new_access_token");
        when(jwtTokenProvider.regenerateRefreshToken(userId)).thenReturn("new_refresh_token");

        UserTokenResponse response = authService.reissue(TEST_REFRESH_TOKEN);

        verify(jwtTokenProvider).validateToken(TEST_REFRESH_TOKEN);
        verify(jwtTokenProvider).getUserIdOnToken(TEST_REFRESH_TOKEN);
        verify(jwtTokenProvider).compareRefreshToken(userId, TEST_REFRESH_TOKEN);
        verify(jwtTokenProvider).regenerateAccessToken(userId);
        verify(jwtTokenProvider).regenerateRefreshToken(userId);

        assertNotNull(response);
        assertEquals("new_access_token", response.accessToken());
        assertEquals("new_refresh_token", response.refreshToken());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 토큰")
    void reissue_InvalidToken_ShouldThrowException() {
        String userId = String.valueOf(TEST_USER_ID);

        doNothing().when(jwtTokenProvider).validateToken(TEST_REFRESH_TOKEN);
        when(jwtTokenProvider.getUserIdOnToken(TEST_REFRESH_TOKEN)).thenReturn(userId);
        when(jwtTokenProvider.compareRefreshToken(userId, TEST_REFRESH_TOKEN)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.reissue(TEST_REFRESH_TOKEN));
        assertEquals(AuthErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout_ShouldDeleteRefreshToken() {
        doNothing().when(jwtTokenProvider).deleteRefreshToken(String.valueOf(TEST_USER_ID));

        authService.logout(TEST_USER_ID);

        verify(jwtTokenProvider).deleteRefreshToken(String.valueOf(TEST_USER_ID));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void withdraw_ShouldUnlinkAndDeleteUser() {
        User user = mock(User.class);
        when(user.getProviderId()).thenReturn(TEST_PROVIDER_ID);

        when(userRetriever.findByUserId(TEST_USER_ID)).thenReturn(user);
        doNothing().when(kakaoProvider).unLink(TEST_PROVIDER_ID);
        doNothing().when(userRemover).deleteByUserId(TEST_USER_ID);
        doNothing().when(jwtTokenProvider).deleteRefreshToken(String.valueOf(TEST_USER_ID));

        authService.withdraw(TEST_USER_ID);

        verify(userRetriever).findByUserId(TEST_USER_ID);
        verify(kakaoProvider).unLink(TEST_PROVIDER_ID);
        verify(userRemover).deleteByUserId(TEST_USER_ID);
        verify(jwtTokenProvider).deleteRefreshToken(String.valueOf(TEST_USER_ID));
    }
}