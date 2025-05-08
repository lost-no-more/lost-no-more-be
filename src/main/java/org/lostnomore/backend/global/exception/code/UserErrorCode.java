package org.lostnomore.backend.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements DefaultErrorCode {
    //404 BAD_REQUEST
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 양식이 잘못되었습니다."),
    ;

    private HttpStatus httpStatus;
    private String message;
}
