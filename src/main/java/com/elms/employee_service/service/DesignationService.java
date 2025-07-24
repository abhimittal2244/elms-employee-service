package com.elms.employee_service.service;

import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.exceptions.DuplicateResourceException;
import com.elms.employee_service.enums.ErrorCode;
import com.elms.employee_service.exceptions.ResourceNotFoundException;
import com.elms.employee_service.model.Designation;
import com.elms.employee_service.dto.DesignationRequestDto;
import com.elms.employee_service.dto.DesignationResponseDto;
import com.elms.employee_service.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DesignationService {
    @Autowired
    private DesignationRepository designationRepository;

    public DesignationResponseDto createDesignation(DesignationRequestDto dto) {
        if (designationRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Designation", dto.getName());
        }

        Designation designation = new Designation();
        designation.setName(dto.getName());

        try {
            Designation saved = designationRepository.save(designation);
            return new DesignationResponseDto(saved.getId(), saved.getName());

        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(ErrorCode.SQL_ERROR,ex.getMostSpecificCause().getMessage());
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }
    }

    public List<DesignationResponseDto> getAllDesignation() {
        List<Designation> designations= designationRepository.findAll();
        return designations.stream().map( designation -> new DesignationResponseDto(
                designation.getId(), designation.getName()
        )).collect(Collectors.toList());
    }

    public DesignationResponseDto getDesignationById(int id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", id));
        return new DesignationResponseDto(designation.getId(), designation.getName());
    }

    public void deleteDesignation(int id) {
            Designation designation = designationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Designation", id));
        try {
            designationRepository.delete(designation);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.SQL_ERROR, e.getMessage());
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }
    }
}
