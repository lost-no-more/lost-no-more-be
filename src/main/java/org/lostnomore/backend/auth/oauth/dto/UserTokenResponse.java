package org.lostnomore.backend.auth.oauth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserTokenResponse {

    private final String accessToken;
    private final String refreshToken;
}