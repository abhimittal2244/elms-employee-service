package com.elms.employee_service.repository;

import com.elms.employee_service.enums.EmployeeStatus;
import com.elms.employee_service.model.Employee;
import com.elms.employee_service.model.Role;
import org.hibernate.mapping.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.management.ValueExp;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findAllByAccountStatus(EmployeeStatus status);
    List<Employee> findAllByManagerId(Employee managerId);

    List<Employee> findAllByRoleIdNot(Role role);
    boolean existsById(int id);
}
