package com.elms.employee_service.exceptions;

import com.elms.employee_service.enums.ErrorCode;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String resource, Object value) {
        super(ErrorCode.RESOURCE_NOT_FOUND, resource + " not found with value: " + value);
    }
}
