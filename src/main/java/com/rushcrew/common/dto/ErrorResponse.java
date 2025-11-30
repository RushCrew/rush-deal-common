package com.rushcrew.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rushcrew.common.global.error.ErrorCode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@Builder
public class ErrorResponse {
    private final int statusCode;
    private final String code;
    private final String message;

    // validation 에러가 없을 경우 JSON에 포함되지 않도록 설정
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    // 정적 팩토리 메서드: 일반 에러용
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .statusCode(errorCode.getHttpStatus().value())
            .code(errorCode.getName())
            .message(errorCode.getMessage())
            .build();
    }

    // 정적 팩토리 메서드: 파라미터 유효성 검사 실패용
    public static ErrorResponse of(ErrorCode errorCode, List<ValidationError> errors) {
        return ErrorResponse.builder()
            .statusCode(errorCode.getHttpStatus().value())
            .code(errorCode.getName())
            .message(errorCode.getMessage())
            .errors(errors)
            .build();
    }

    // 내부 클래스: 유효성 검사 상세 내용
    @Getter
    @Builder
    public static class ValidationError {
        private final String field;
        private final String message;

        public static ValidationError of(FieldError fieldError) {
            return ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
        }
    }
}