package com.elms.employee_service.controller;

import com.elms.employee_service.dto.DesignationRequestDto;
import com.elms.employee_service.dto.DesignationResponseDto;
import com.elms.employee_service.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee/designations")
public class DesignationController {
    @Autowired
    private DesignationService designationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DesignationResponseDto> createDesignation(@RequestBody DesignationRequestDto designation) {
        return ResponseEntity.ok(designationService.createDesignation(designation));
    }


    @GetMapping
    public ResponseEntity<List<DesignationResponseDto>> getAllDesignations() {
        return ResponseEntity.ok(designationService.getAllDesignation());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
//    @RequestMapping("/{id}")
    public ResponseEntity<DesignationResponseDto> getDesignationById(@PathVariable int id) {
        return ResponseEntity.ok(designationService.getDesignationById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable int id) {
        designationService.deleteDesignation(id);
        return ResponseEntity.noContent().build();
    }
}
