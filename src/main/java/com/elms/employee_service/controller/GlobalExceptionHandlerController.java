package com.elms.employee_service.controller;

import com.elms.employee_service.dto.ErrorResponse;
import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, HttpServletRequest request) {
//        ErrorResponse response = new ErrorResponse(
//                LocalDateTime.now(),
//                "Internal server error",
//                "GENERIC_ERROR",
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
}
