package org.lostnomore.backend.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserNotificationErrorCode implements DefaultErrorCode {
    //403 FORBIDDEN
    USER_NOTIFICATION_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 알림에 대한 권한이 없습니다."),
    //404 BAD_REQUEST
    USER_NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 알림을 찾을 수 없습니다."),
    ;

    private HttpStatus httpStatus;
    private String message;
}
