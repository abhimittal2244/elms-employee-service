package com.elms.employee_service.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PasswordUpdateDto {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
