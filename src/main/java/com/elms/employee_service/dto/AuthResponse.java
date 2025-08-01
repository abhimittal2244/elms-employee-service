package com.elms.employee_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
