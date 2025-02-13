package org.lostnomore.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.provider.OAuthProviderFinder;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserCreator;
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

    public String getCodeLink(String provider) {
        return oAuthProviderFinder.getOAuthProvider(provider).getCodeUrl();
    }

    @Transactional
    public String oauthLogin(String provider, String code) {
        String accessToken = oAuthProviderFinder.getOAuthProvider(provider).getAccessToken(code);
        String email = oAuthProviderFinder.getOAuthProvider(provider).getUserInfo(accessToken);
        SocialType socialType = SocialType.valueOf(provider.toUpperCase());

        User existedUser = userRetriever.findByEmailAndSocialType(email, socialType);

        if (existedUser == null) {
            User newUser = userService.register(email, socialType);
            userCreator.save(newUser);
        }

        return email;
    }
}
