package com.elms.employee_service.service;

import com.elms.employee_service.enums.ErrorCode;
import com.elms.employee_service.exceptions.*;
import com.elms.employee_service.model.Department;
import com.elms.employee_service.dto.DepartmentRequestDto;
import com.elms.employee_service.dto.DepartmentResponseDto;
import com.elms.employee_service.repository.DepartmentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService{

    @Autowired
    private DepartmentRepository departmentRepository;

    private static final Logger logger = LogManager.getLogger(DepartmentService.class);

    public DepartmentResponseDto createDepartment(DepartmentRequestDto dto) {
        logger.info("Received request to create department: {}", dto.getName());

        if (departmentRepository.existsByName(dto.getName())) {
            logger.error("Department creation failed: Department already exists with name {}", dto.getName());
            throw new DuplicateResourceException("Department",dto.getName());
        }

        Department dept = new Department();
        dept.setName(dto.getName());

        try {
            Department saved = departmentRepository.save(dept);
            logger.info("Department created successfully with ID: {}", saved.getId());
            return new DepartmentResponseDto(saved.getId(), saved.getName());
        } catch (DataIntegrityViolationException ex) {
            logger.error("SQL Error while saving department: {}", ex.getMostSpecificCause().getMessage(), ex);
            throw new ApiException(ErrorCode.SQL_ERROR, "Failed to save department: "+ex.getMostSpecificCause().getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while creating department: {}", ex.getMessage(), ex);
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "Unknown exception caught! " + ex.getMessage());
        }
    }

    public List<DepartmentResponseDto> getAllDepartments() {
        logger.info("Fetching all departments");
        List<Department> departments = departmentRepository.findAll();
        logger.debug("Total departments found: {}", departments.size());
        return departments.stream().map(department -> new DepartmentResponseDto(
                department.getId(), department.getName()
        )).collect(Collectors.toList());
    }

    public DepartmentResponseDto getDepartmentById(int id) {
        logger.info("Fetching department by ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Department not found with ID: {}", id);
                    return new ResourceNotFoundException("Department", id);
                });
        return new DepartmentResponseDto(department.getId(), department.getName());
    }

    public void deleteDepartment(int id) {
        logger.info("Deleting department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Department not found for deletion with ID: {}", id);
                    return new ResourceNotFoundException("Department", id);
                });
        try{
            departmentRepository.delete(department);
            logger.info("Department deleted successfully with ID: {}", id);
        } catch (DataIntegrityViolationException ex) {
            logger.error("SQL Error while deleting department: {}", ex.getMostSpecificCause().getMessage(), ex);
            throw new ApiException(ErrorCode.SQL_ERROR, ex.getMostSpecificCause().getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while deleting department with ID {}: {}", id, ex.getMessage(), ex);
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }
    }
}
