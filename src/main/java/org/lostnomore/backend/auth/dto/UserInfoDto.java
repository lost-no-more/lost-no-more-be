package org.lostnomore.backend.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInfoDto {

    private final String providerId;
    private final String email;
}
