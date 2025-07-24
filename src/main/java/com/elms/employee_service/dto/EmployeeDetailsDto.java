package com.elms.employee_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Setter
@AllArgsConstructor
public class EmployeeDetailsDto {
    private int employeeId;
    private String fName;
    private String lName;
    private String email;
    private LocalDate joinDate;
    private String phone;
    private String accountStatus;
    private String role;
    private String manager;
    private String designation;
    private String department;
}
