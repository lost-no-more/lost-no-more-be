package org.lostnomore.backend.auth.util;

import static org.lostnomore.backend.global.exception.code.AuthErrorCode.INVALID_ACCESS_TOKEN;

import org.lostnomore.backend.global.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class BearerAuthorizationExtractor {

    private static final String BEARER_TYPE = "Bearer ";

    public String extractAccessToken(String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length()).trim();
        }
        throw new BusinessException(INVALID_ACCESS_TOKEN);
    }
}