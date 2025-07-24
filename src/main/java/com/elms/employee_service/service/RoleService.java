package com.elms.employee_service.service;

import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.exceptions.DuplicateResourceException;
import com.elms.employee_service.enums.ErrorCode;
import com.elms.employee_service.model.Role;
import com.elms.employee_service.dto.RoleRequestDto;
import com.elms.employee_service.dto.RoleResponseDto;
import com.elms.employee_service.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public RoleResponseDto createRole(RoleRequestDto dto) {
        if (roleRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Role",dto.getName());
        }

        Role role = new Role();
        role.setName(dto.getName().toUpperCase());
        try {
            Role savedRole = roleRepository.save(role);
            return new RoleResponseDto(savedRole.getId(), savedRole.getName());
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(ErrorCode.SQL_ERROR, ex.getMessage());
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }

    }


    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map( role -> new RoleResponseDto(
            role.getId(), role.getName()
        )).collect(Collectors.toList());
    }
}
