package com.elms.employee_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class RoleRequestDto {
    @NotNull(message = "Role Name Field can not be empty")
    private String name;
}
