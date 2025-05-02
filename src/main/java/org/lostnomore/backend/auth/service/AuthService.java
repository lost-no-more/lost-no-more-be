package org.lostnomore.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.oauth.dto.UserTokenResponse;
import org.lostnomore.backend.auth.oauth.kakao.KakaoProvider;
import org.lostnomore.backend.auth.oauth.kakao.dto.KakaoUserResponse;
import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
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

    private final KakaoProvider kakaoProvider;
    private final UserService userService;
    private final UserRetriever userRetriever;
    private final UserCreator userCreator;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRemover userRemover;

    public String getCodeLink() {
        return kakaoProvider.getCodeUrl();
    }

    @Transactional
    public UserTokenResponse oauthLogin(String code) {

        KakaoUserResponse userInfo = kakaoProvider.getUserInfo(code);

        User user;
        user = userRetriever.findByProviderId(userInfo.id());

        if (user == null) {
            user = userService.register(userInfo.id(), userInfo.kakaoAccount().email());
            userCreator.save(user);
        }

        Long userId = user.getId();

        return jwtTokenProvider.createLoginToken(String.valueOf(userId));
    }

    @Transactional
    public UserTokenResponse reissue(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);
        String userId = jwtTokenProvider.getUserIdOnToken(refreshToken);

        if (!jwtTokenProvider.compareRefreshToken(userId, refreshToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }

        String regeneratedAccessToken = jwtTokenProvider.regenerateAccessToken(userId);
        String regeneratedRefreshToken = jwtTokenProvider.regenerateRefreshToken(userId);

        return new UserTokenResponse(regeneratedAccessToken, regeneratedRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        jwtTokenProvider.deleteRefreshToken(String.valueOf(userId));
    }

    @Transactional
    public void withdraw(Long userId) {

        Long providerId = userRetriever.findByUserId(userId).getProviderId();
        kakaoProvider.unLink(providerId);

        userRemover.deleteByUserId(userId);
        jwtTokenProvider.deleteRefreshToken(String.valueOf(userId));
    }
}
