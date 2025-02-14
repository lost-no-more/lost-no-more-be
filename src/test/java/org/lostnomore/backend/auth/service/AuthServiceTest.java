package org.lostnomore.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.auth.dto.UserTokenDto;
import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.auth.provider.OAuthProvider;
import org.lostnomore.backend.auth.provider.OAuthProviderFinder;
import org.lostnomore.backend.auth.repository.RefreshTokenRepository;
import org.lostnomore.backend.auth.util.BearerAuthorizationExtractor;
import org.lostnomore.backend.common.ServiceTest;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserCreator;
import org.lostnomore.backend.user.manager.UserRetriever;
import org.lostnomore.backend.user.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest extends ServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private OAuthProviderFinder oAuthProviderFinder;

    @Mock
    private UserRetriever userRetriever;

    @Mock
    private UserCreator userCreator;

    @Mock
    private BearerAuthorizationExtractor bearerAuthorizationExtractor;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private final String refreshToken = "refresh-token";
    private final String accessToken = "access-token";
    private final String provider = "google";
    private final String oauthCode = "oauth-code";
    private final String email = "test@example.com";
    private final String name = "test_name";
    private final Long userId = 1L;
    private final SocialType socialType = SocialType.GOOGLE;

    @Test
    void 기존_유저가_로그인_하면_DB에_저장되지_않는다() {
        // given
        OAuthProvider mockOAuthProvider = mock(OAuthProvider.class);
        when(oAuthProviderFinder.getOAuthProvider(provider)).thenReturn(mockOAuthProvider);
        when(mockOAuthProvider.getAccessToken(oauthCode)).thenReturn(accessToken);
        when(mockOAuthProvider.getUserInfo(accessToken)).thenReturn(email);

        User existingUser = User.builder()
                .email(email)
                .name(name)
                .socialType(socialType)
                .build();
        User spyUser = spy(existingUser);
        when(spyUser.getId()).thenReturn(userId);

        when(userRetriever.findByEmailAndSocialType(email, socialType)).thenReturn(spyUser);

        doReturn(new UserTokenDto(accessToken, refreshToken)).when(jwtTokenProvider).createLoginToken(anyLong());

        // when
        authService.oauthLogin(provider, oauthCode);

        // then
        verify(userCreator, never()).save(any(User.class));
    }

    @Test
    void 토큰들이_만료되지_않으면_기존_토큰_반환() {
        // given
        when(bearerAuthorizationExtractor.extractAccessToken(anyString())).thenReturn(accessToken);
        when(jwtTokenProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken)).thenReturn(false);
        when(jwtTokenProvider.isValidRefreshAndValidAccess(refreshToken, accessToken)).thenReturn(true);
        // when
        UserTokenDto reissue = authService.reissue(refreshToken, anyString());

        // then
        assertThat(reissue.getAccessToken()).isEqualTo(accessToken);
        assertThat(reissue.getRefreshToken()).isNull();
    }

    @Test
    void 토큰들이_모두_만료되면_오류() {
        // given
        when(bearerAuthorizationExtractor.extractAccessToken(anyString())).thenReturn(accessToken);
        when(jwtTokenProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken)).thenReturn(false);
        when(jwtTokenProvider.isValidRefreshAndValidAccess(refreshToken, accessToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue(refreshToken, anyString()))
                .isInstanceOf(BusinessException.class);
    }
}