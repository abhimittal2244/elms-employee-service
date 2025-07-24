package com.elms.employee_service.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class EmployeeUploadResultDto {
    private EmployeeDetailsDto employee;
    private EmployeeRegisterDto employeeInput; // to know which row caused error
    private boolean success;
    private String error;

    // success constructor
    public static EmployeeUploadResultDto success(EmployeeDetailsDto emp) {
        EmployeeUploadResultDto res = new EmployeeUploadResultDto();
        res.employee = emp;
        res.success = true;
        return res;
    }

    // failure constructor
    public static EmployeeUploadResultDto failure(EmployeeRegisterDto input, String error) {
        EmployeeUploadResultDto res = new EmployeeUploadResultDto();
        res.employeeInput = input;
        res.success = false;
        res.error = error;
        return res;
    }

    // getters and setters ...
}

