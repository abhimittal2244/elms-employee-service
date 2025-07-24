package com.elms.employee_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DesignationRequestDto {
    @NotNull(message = "Department Name can not be Empty")
    private String name;
}
