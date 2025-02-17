package org.lostnomore.backend.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SubscribeErrorCode implements DefaultErrorCode {
    //403 FORBIDDEN
    SUBSCRIBE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 구독에 대한 권한이 없습니다."),

    //404 BAD_REQUEST
    SUBSCRIBE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 구독을 찾을 수 없습니다."),

    //409 CONFLICT
    SUBSCRIBE_DUPLICATE(HttpStatus.CONFLICT, "이미 등록된 구독 정보입니다.")
    ;

    private HttpStatus httpStatus;
    private String message;
}
