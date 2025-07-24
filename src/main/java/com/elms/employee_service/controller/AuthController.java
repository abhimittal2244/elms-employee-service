package com.elms.employee_service.controller;

import com.elms.employee_service.dto.AuthResponse;
import com.elms.employee_service.dto.EmployeeDetailsDto;
import com.elms.employee_service.dto.EmployeeLoginDto;
import com.elms.employee_service.dto.EmployeeRegisterDto;
import com.elms.employee_service.service.AuthService;
import com.elms.employee_service.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/employee/auth")
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<EmployeeDetailsDto> createEmployee(@RequestBody EmployeeRegisterDto employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee, false));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginAuth(@RequestBody EmployeeLoginDto emp) {
        AuthResponse tok = authService.authenticate(emp);
        return ResponseEntity.ok(tok);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

}
