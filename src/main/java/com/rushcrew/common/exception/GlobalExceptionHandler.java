package com.rushcrew.common.exception;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.common.dto.ErrorResponse;
import com.rushcrew.common.dto.ErrorResponse.ValidationError;
import com.rushcrew.common.global.error.CommonErrorCode;
import com.rushcrew.common.global.error.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 실행 중 발생한 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        log.error("[Common Error] BusinessException: {}", e.getErrorCode().getName());
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * @Valid, @Validated 어노테이션을 이용한 유효성 검사 실패 시 처리
     * CommonErrorCode.INVALID_PARAMETER 를 사용하며 상세 에러 목록을 포함함
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[Common Error] MethodArgumentNotValidException: {}", e.getMessage());

        List<ErrorResponse.ValidationError> validationErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(ErrorResponse.ValidationError::of)
            .collect(Collectors.toList());

        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, validationErrors);

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * 나머지 모든 예외 처리 (서버 내 오류)
     * CommonErrorCode.INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception e) {
        log.error("[Common Error] Internal Server Error", e);

        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.error(errorResponse));
    }
}
