package com.elms.employee_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DepartmentRequestDto {
    @NotNull(message = "Department Name can not be Empty")
    private String name;
}
