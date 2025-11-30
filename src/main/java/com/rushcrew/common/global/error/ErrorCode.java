package com.rushcrew.common.global.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();
    String getName(); // Enum의 이름 (예: INVALID_INPUT)
    String getMessage();
}