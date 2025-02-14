package org.lostnomore.backend.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements DefaultErrorCode {
    //400 BAD_REQUEST
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NON_EXISTENT_SOCIAL_TYPE(HttpStatus.NOT_FOUND,"해당 소셜로그인을 제공해주지 않습니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST,"잘못된 코드입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"잘못된 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "올바르지 않은 형식의 AccessToken입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "올바르지 않은 형식의 RefreshToken입니다."),
    EXPIRED_PERIOD_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "만료된 AccessToken입니다."),
    EXPIRED_PERIOD_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "만료된 RefreshToken입니다."),
    FAIL_TO_VALIDATE_TOKEN(HttpStatus.BAD_REQUEST, "토큰 유효성 검사 중 오류가 발생했습니다."),

    // 500 INTERNAL_SEVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    ;

    private HttpStatus httpStatus;
    private String message;
}
