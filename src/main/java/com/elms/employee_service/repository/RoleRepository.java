package com.elms.employee_service.repository;

import com.elms.employee_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByName(String name);
}
