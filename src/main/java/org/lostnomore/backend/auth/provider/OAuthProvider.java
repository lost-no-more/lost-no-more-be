package org.lostnomore.backend.auth.provider;

import org.lostnomore.backend.auth.dto.UserInfoDto;

public interface OAuthProvider {

    String getCodeUrl();

    UserInfoDto getUserInfo(final String accessToken);

    String getAccessToken(final String code);

    void unLink(final String providerId, final String code);
}
