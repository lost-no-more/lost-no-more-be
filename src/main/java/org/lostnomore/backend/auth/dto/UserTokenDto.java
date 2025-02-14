package org.lostnomore.backend.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserTokenDto {

    private final String accessToken;
    private final String refreshToken;
}