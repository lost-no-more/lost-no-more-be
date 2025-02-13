package org.lostnomore.backend.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements DefaultErrorCode{

    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST,"필수 파라미터가 누락되었습니다."),
    ;

    private HttpStatus httpStatus;
    private String message;
}
