package com.elms.employee_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Setter
@Getter
@AllArgsConstructor
public class EmployeeMetadataDto {
    private int employeeId;
    private String fullName;
    private String email;
    private LocalDate joinDate;
    private String phone;
    private String role;
    private int managerId;
    private int designationId;
    private int departmentId;
}
