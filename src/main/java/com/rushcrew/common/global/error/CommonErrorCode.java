package com.rushcrew.common.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "파라미터가 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR","서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String name;
    private final String message;

    CommonErrorCode(HttpStatus httpStatus, String name, String message) {
        this.httpStatus = httpStatus;
        this.name = name;
        this.message = message;
    }
}