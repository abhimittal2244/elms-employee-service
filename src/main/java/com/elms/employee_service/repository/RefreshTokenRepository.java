package com.elms.employee_service.repository;

import com.elms.employee_service.model.Employee;
import com.elms.employee_service.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByEmployeeId(Employee employee);
}
