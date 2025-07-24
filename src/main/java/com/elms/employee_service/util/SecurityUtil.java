package com.elms.employee_service.util;

import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.enums.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "User not authenticated");
        }
        return authentication.getName();
    }
}
