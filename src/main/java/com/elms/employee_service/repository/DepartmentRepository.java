package com.elms.employee_service.repository;

import com.elms.employee_service.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    boolean existsByName(String name);
}
