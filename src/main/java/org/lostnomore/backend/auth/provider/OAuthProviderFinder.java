package org.lostnomore.backend.auth.provider;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.dto.OAuthType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuthProviderFinder {

    private final Map<OAuthType, OAuthProvider> oauthProvider = new HashMap<>();
    private final GoogleProvider googleProvider;
    private final KakaoProvider kakaoProvider;

    @PostConstruct
    public void init() {
        oauthProvider.put(OAuthType.GOOGLE, googleProvider);
        oauthProvider.put(OAuthType.KAKAO, kakaoProvider);
    }

    public OAuthProvider getOAuthProvider(final String provider) {
        final OAuthType key = OAuthType.find(provider);
        return oauthProvider.get(key);
    }
}