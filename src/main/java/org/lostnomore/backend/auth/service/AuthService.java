package org.lostnomore.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.domain.RefreshToken;
import org.lostnomore.backend.auth.dto.UserInfoDto;
import org.lostnomore.backend.auth.dto.UserTokenDto;
import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.auth.provider.OAuthProviderFinder;
import org.lostnomore.backend.auth.repository.RefreshTokenRepository;
import org.lostnomore.backend.auth.util.BearerAuthorizationExtractor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.lostnomore.backend.notification.manager.UserNotificationRemover;
import org.lostnomore.backend.subscribe.manager.SubscribeRemover;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserCreator;
import org.lostnomore.backend.user.manager.UserRemover;
import org.lostnomore.backend.user.manager.UserRetriever;
import org.lostnomore.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OAuthProviderFinder oAuthProviderFinder;
    private final UserService userService;
    private final UserRetriever userRetriever;
    private final UserCreator userCreator;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BearerAuthorizationExtractor bearerExtractor;
    private final SubscribeRemover subscribeRemover;
    private final UserNotificationRemover userNotificationRemover;
    private final UserRemover userRemover;

    public String getCodeLink(String provider) {
        return oAuthProviderFinder.getOAuthProvider(provider).getCodeUrl();
    }

    @Transactional
    public UserTokenDto oauthLogin(String provider, String code) {
        String oauthAccessToken = oAuthProviderFinder.getOAuthProvider(provider).getAccessToken(code);
        UserInfoDto userInfo = oAuthProviderFinder.getOAuthProvider(provider).getUserInfo(oauthAccessToken);

        SocialType socialType = SocialType.valueOf(provider.toUpperCase());

        User user;
        user = userRetriever.findByProviderId(userInfo.getProviderId());

        if (user == null) {
            user = userService.register(userInfo.getProviderId(), userInfo.getEmail(), socialType);
            userCreator.save(user);
        }

        Long userId = user.getId();
        UserTokenDto loginToken = jwtTokenProvider.createLoginToken(userId);
        RefreshToken savedRefreshToken = new RefreshToken(loginToken.getRefreshToken(), userId);
        refreshTokenRepository.save(savedRefreshToken);

        return loginToken;
    }

    @Transactional
    public UserTokenDto reissue(String refreshTokenRequest, String requestHeader) {
        jwtTokenProvider.validateRefreshToken(refreshTokenRequest);
        String expiredAccessToken = bearerExtractor.extractAccessToken(requestHeader);

        final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        Long userId = jwtTokenProvider.getExpiredSubject(expiredAccessToken);

        if (!refreshToken.getUserId().equals(userId)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String regeneratedAccessToken = jwtTokenProvider.regenerateAccessToken(userId);
        String regeneratedRefreshToken = jwtTokenProvider.regenerateRefreshToken(userId);

        refreshTokenRepository.deleteById(refreshTokenRequest);
        refreshTokenRepository.save(new RefreshToken(regeneratedRefreshToken, userId));

        return new UserTokenDto(regeneratedAccessToken, regeneratedRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    @Transactional
    public void withdraw(String provider, String code, Long userId, String refreshToken) {

        String providerId = userRetriever.findByUserId(userId).getProviderId();
        oAuthProviderFinder.getOAuthProvider(provider).unLink(providerId, code);

        refreshTokenRepository.deleteById(refreshToken);

        subscribeRemover.deleteByUserId(userId);
        userNotificationRemover.deleteByUserId(userId);
        userRemover.deleteByUserId(userId);
    }
}
