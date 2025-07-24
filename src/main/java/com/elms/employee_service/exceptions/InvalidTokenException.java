package com.elms.employee_service.exceptions;

import com.elms.employee_service.enums.ErrorCode;

public class InvalidTokenException extends ApiException{
    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_JWT_TOKEN, message);
    }
}
