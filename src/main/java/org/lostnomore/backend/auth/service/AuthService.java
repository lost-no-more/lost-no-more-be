package org.lostnomore.backend.auth.service;

import static org.lostnomore.backend.global.exception.code.AuthErrorCode.FAIL_TO_VALIDATE_TOKEN;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lostnomore.backend.auth.domain.RefreshToken;
import org.lostnomore.backend.auth.dto.UserTokenDto;
import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.auth.provider.OAuthProviderFinder;
import org.lostnomore.backend.auth.repository.RefreshTokenRepository;
import org.lostnomore.backend.auth.util.BearerAuthorizationExtractor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserCreator;
import org.lostnomore.backend.user.manager.UserRetriever;
import org.lostnomore.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final OAuthProviderFinder oAuthProviderFinder;
    private final UserService userService;
    private final UserRetriever userRetriever;
    private final UserCreator userCreator;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BearerAuthorizationExtractor bearerExtractor;

    public String getCodeLink(String provider) {
        return oAuthProviderFinder.getOAuthProvider(provider).getCodeUrl();
    }

    @Transactional
    public UserTokenDto oauthLogin(String provider, String code) {
        String oauthAccessToken = oAuthProviderFinder.getOAuthProvider(provider).getAccessToken(code);
        String email = oAuthProviderFinder.getOAuthProvider(provider).getUserInfo(oauthAccessToken);
        SocialType socialType = SocialType.valueOf(provider.toUpperCase());

        User user;
        user = userRetriever.findByEmailAndSocialType(email, socialType);

        if (user == null) {
            user = userService.register(email, socialType);
            userCreator.save(user);
        }

        Long userId = user.getId();
        UserTokenDto loginToken = jwtTokenProvider.createLoginToken(userId);
        RefreshToken savedRefreshToken = new RefreshToken(loginToken.getRefreshToken(), userId);
        refreshTokenRepository.save(savedRefreshToken);

        return loginToken;
    }

    public UserTokenDto reissue(String refreshTokenRequest, String authorizationHeader) {
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);

        if (jwtTokenProvider.isValidRefreshAndInvalidAccess(refreshTokenRequest, accessToken)) {
            final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                    .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

            Long userId = refreshToken.getUserId();

            String regeneratedAccessToken = jwtTokenProvider.regenerateAccessToken(userId);
            String regeneratedRefreshToken = jwtTokenProvider.regenerateRefreshToken(userId);

            refreshTokenRepository.deleteById(refreshTokenRequest);

            RefreshToken savedRefreshToken = new RefreshToken(regeneratedRefreshToken, userId);
            refreshTokenRepository.save(savedRefreshToken);

            return new UserTokenDto(regeneratedAccessToken, regeneratedRefreshToken);
        }

        if (jwtTokenProvider.isValidRefreshAndValidAccess(refreshTokenRequest, accessToken)) {
            return new UserTokenDto(accessToken, null);
        }

        throw new BusinessException(FAIL_TO_VALIDATE_TOKEN);
    }
}
