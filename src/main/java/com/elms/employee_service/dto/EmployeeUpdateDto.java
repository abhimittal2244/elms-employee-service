package com.elms.employee_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class EmployeeUpdateDto {
    @NotBlank(message = "First Name cannot be empty")
    private String fName;

    @NotBlank(message = "Last Name cannot be empty")
    private String lName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = "Password must contain upper case, lower case, a number, and a special character"
    )
    private String phone;

    @NotBlank(message = "Role Id is required")
    private int roleId;

    private int managerId;

    @NotBlank(message = "Designation Id is required")
    private int designationId;

    @NotBlank(message = "Department Id is required")
    private int departmentId;
}
