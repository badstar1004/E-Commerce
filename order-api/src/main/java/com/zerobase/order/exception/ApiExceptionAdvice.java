package com.zerobase.order.exception;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomException.CustomExceptionResponse> exceptionHandler(
        HttpServletRequest request, final CustomException ex){
        return ResponseEntity
            .status(ex.getStatus())
            .body(CustomException.CustomExceptionResponse.builder()
                .code(ex.getErrorCode().name())
                .message(ex.getMessage())
                .status(ex.getStatus())
                .build());
    }

}
