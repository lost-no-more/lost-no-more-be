package org.lostnomore.backend.auth.oauth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInfoResponse {

    private final String providerId;
    private final String email;
}
