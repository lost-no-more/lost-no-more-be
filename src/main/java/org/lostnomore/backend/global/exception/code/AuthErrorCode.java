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
    // 500 INTERNAL_SEVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    ;

    private HttpStatus httpStatus;
    private String message;
}
