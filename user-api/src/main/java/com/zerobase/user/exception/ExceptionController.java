package com.zerobase.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler({
        CustomException.class
    })
    public ResponseEntity<ExceptionResponse> customRequestException(final CustomException ex) {
        log.warn("API Exception : {}", ex.getErrorCode());
        return ResponseEntity.badRequest().body(new ExceptionResponse(ex.getErrorCode(),
            ex.getMessage()));
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class ExceptionResponse{
        private ErrorCode errorCode;
        private String message;
    }

}
