package org.lostnomore.backend.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ItemErrorCode implements DefaultErrorCode {
    //404 BAD_REQUEST
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이템을 찾을 수 없습니다."),
    ;

    private HttpStatus httpStatus;
    private String message;
}
