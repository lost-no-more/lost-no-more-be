package org.lostnomore.backend.global.exception;

import lombok.Getter;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;

@Getter
public class ExpiredPeriodJwtException extends BusinessException {

    public ExpiredPeriodJwtException(final AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}