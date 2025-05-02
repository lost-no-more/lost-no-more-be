package org.lostnomore.backend.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements DefaultErrorCode {
    // 400 BAD_REQUEST
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST,"잘못된 코드입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "올바르지 않은 형식의 토큰입니다."),
    EXPIRED_PERIOD_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
    FAIL_TO_VALIDATE_TOKEN(HttpStatus.BAD_REQUEST, "토큰 유효성 검사 중 오류가 발생했습니다."),

    // 404 NOT_FOUND
    NON_EXISTENT_SOCIAL_TYPE(HttpStatus.NOT_FOUND,"해당 소셜로그인을 제공해주지 않습니다."),
    NON_EXISTENT_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),

    // 500 INTERNAL_SEVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    ;

    private HttpStatus httpStatus;
    private String message;
}
