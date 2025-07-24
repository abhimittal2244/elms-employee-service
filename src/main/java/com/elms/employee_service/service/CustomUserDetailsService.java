package com.elms.employee_service.service;

import com.elms.employee_service.exceptions.ResourceNotFoundException;
import com.elms.employee_service.model.Employee;
import com.elms.employee_service.repository.EmployeeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("Employee", email);
                });

        String roleName = employee.getRoleId().getName();
        logger.debug("User found: {}, Role: {}", email, roleName);

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+roleName));

        logger.info("Successfully loaded user details for: {}", email);
        return new User(
                employee.getEmail(),
                employee.getPasswordHash(),
                authorities
        );
    }

}
