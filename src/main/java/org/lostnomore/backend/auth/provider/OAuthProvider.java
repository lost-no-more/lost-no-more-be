package org.lostnomore.backend.auth.provider;

public interface OAuthProvider {

    String getCodeUrl();

    String getUserInfo(final String accessToken);

    String getAccessToken(final String code);

}
