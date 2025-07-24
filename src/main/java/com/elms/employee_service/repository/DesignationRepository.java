package com.elms.employee_service.repository;

import com.elms.employee_service.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation, Integer> {
    boolean existsByName(String name);
}
