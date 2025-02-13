package org.lostnomore.backend.global.exception;

import lombok.Getter;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;

@Getter
public class InvalidJwtException extends BusinessException {

    public InvalidJwtException(final AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}