package org.lostnomore.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lostnomore.backend.global.exception.code.DefaultErrorCode;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private final DefaultErrorCode errorCode;
}
