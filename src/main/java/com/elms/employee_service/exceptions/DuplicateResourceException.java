package com.elms.employee_service.exceptions;

import com.elms.employee_service.enums.ErrorCode;

public class DuplicateResourceException extends ApiException {
    public DuplicateResourceException(String resource, Object value) {
        super(ErrorCode.DUPLICATE_RESOURCE, resource + " '" + value + "' Already Exists!! ");
    }
}