package org.lostnomore.backend.auth.dto;

import java.util.Arrays;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;

public enum OAuthType {

    GOOGLE, KAKAO;

    public static OAuthType find(final String provider) {
        return Arrays.stream(OAuthType.values())
                .filter(type -> type.name().equals(provider.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(AuthErrorCode.NON_EXISTENT_SOCIAL_TYPE));
    }
}