package com.elms.employee_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
public enum ErrorCode {
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Requested resource not found", HttpStatus.NOT_FOUND),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "Resource already exists", HttpStatus.CONFLICT),
    INVALID_INPUT("INVALID_INPUT", "Invalid request data", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "Invalid Credentials", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED("UNAUTHENTICATED", "Invalid Credentials", HttpStatus.BAD_REQUEST),
    EXPIRED_JWT_TOKEN("EXPIRED_JWT_TOKEN", "Expired Token Received", HttpStatus.UNAUTHORIZED),
    SQL_ERROR("SQL_ERROR", "Error while communicating with database", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_JWT_TOKEN("INVALID_JWT_TOKEN", "Invalid Token Received", HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR("INTERNAL_ERROR", "An internal error occurred with server", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

//    // Helper method to create a ResponseEntity with no body
//    public ResponseEntity<ErrorResponse> toResponseEntity() {
//        ErrorResponse errorResponse = new ErrorResponse(this.code, this.message);
//        return new ResponseEntity<>(errorResponse, this.httpStatus);
//    }
//
//    // Helper method to create a ResponseEntity with custom body (optional)
//    public <T> ResponseEntity<T> toResponseEntity(T body) {
//        return new ResponseEntity<>(body, this.httpStatus);
//    }
//
//    // Nested class for consistent error response structure
//    @Getter
//    @AllArgsConstructor
//    public static class ErrorResponse {
//        private final String errorCode;
//        private final String errorMessage;
//    }
}
