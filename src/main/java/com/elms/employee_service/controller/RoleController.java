package com.elms.employee_service.controller;

import com.elms.employee_service.dto.RoleRequestDto;
import com.elms.employee_service.dto.RoleResponseDto;
import com.elms.employee_service.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@RequestBody RoleRequestDto role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }


    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
