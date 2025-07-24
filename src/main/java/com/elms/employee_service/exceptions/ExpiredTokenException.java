package com.elms.employee_service.exceptions;

import com.elms.employee_service.enums.ErrorCode;

public class ExpiredTokenException extends ApiException{
    public ExpiredTokenException(String message) {
        super(ErrorCode.EXPIRED_JWT_TOKEN, message);
    }
}
