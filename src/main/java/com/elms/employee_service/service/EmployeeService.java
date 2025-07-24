package com.elms.employee_service.service;

import com.elms.employee_service.enums.EmployeeStatus;
import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.exceptions.DuplicateResourceException;
import com.elms.employee_service.enums.ErrorCode;
import com.elms.employee_service.exceptions.ResourceNotFoundException;
import com.elms.employee_service.feignClient.LeaveClient;
import com.elms.employee_service.model.Department;
import com.elms.employee_service.model.Designation;
import com.elms.employee_service.model.Employee;
import com.elms.employee_service.dto.*;
import com.elms.employee_service.model.Role;
import com.elms.employee_service.repository.*;
import com.elms.employee_service.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private LeaveClient leaveClient;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private static final Logger logger = LogManager.getLogger(EmployeeService.class);

//    @Transactional
//    public EmployeeDetailsDto createEmployee(EmployeeRegisterDto emp) {
//        try {
//            System.out.println(emp);
//            System.out.println("now 1");
//            if (employeeRepository.findByEmail(emp.getEmail()).isPresent()) {
//                System.out.println("now 2");
//                throw new DuplicateResourceException("Employee", emp.getEmail());
//            }
//            System.out.println("now 3");
//            Employee employee = new Employee();
//            employee.setFName(emp.getFirstName());
//            employee.setLName(emp.getLastName());
//            employee.setEmail(emp.getEmail());
//            employee.setPhone(emp.getPhone());
//            employee.setJoinDate(emp.getJoinDate());
//            employee.setAccountStatus(EmployeeStatus.PENDING);
//            System.out.println("now 4");
//            Role empRole = roleRepository.findById(emp.getRoleId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Role", String.valueOf(emp.getRoleId())));
//            employee.setRoleId(empRole);
//            System.out.println("now 5");
//            Department empDepartment = departmentRepository.findById(emp.getDepartmentId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Department", String.valueOf(emp.getDepartmentId())));
//            employee.setDepartmentId(empDepartment);
//            System.out.println("now 6");
//            Designation empDesignation = designationRepository.findById(emp.getDesignationId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Designation", String.valueOf(emp.getDesignationId())));
//            employee.setDesignationId(empDesignation);
//            System.out.println("now 7");
//            employee.setPasswordHash(passwordEncoder.encode(emp.getPassword()));
//
//            System.out.println("now 8");
//            if (emp.getManagerId() != 0 && !"ADMIN".contentEquals(empRole.getName())) {
//                Employee manager = employeeRepository.findById(emp.getManagerId())
//                        .orElseThrow(() -> new ResourceNotFoundException("Manager", String.valueOf(emp.getManagerId())));
//                String managerRole = manager.getRoleId().getName();
//                if (managerRole.contentEquals("MANAGER") || managerRole.contentEquals("ADMIN"))
//                    employee.setManagerId(manager);
//                else
//                    throw new IllegalArgumentException("Provided Manager ID does not belong to any Existing Manager or Admin Id's ");
//            } else if (emp.getManagerId() == 0 && empRole.getName().contentEquals("ADMIN")) {
//                employee.setManagerId(null);
//            } else if (emp.getManagerId() == 0 && !empRole.getName().contentEquals("ADMIN")) {
//                throw new IllegalArgumentException("Manager Id is not provided");
//            } else {
//                throw new IllegalArgumentException("ADMIN role can not have manager");
//            }
//            System.out.println("now 9");
//            employeeRepository.save(employee);
//        String managerName = (employee.getManagerId() != null)
//                ? employee.getManagerId().getFullName()
//                : "None";
//
//        return new EmployeeDetailsDto(
//                employee.getId(),
//                employee.getFName(),
//                employee.getLName(),
//                employee.getEmail(),
//                employee.getJoinDate(),
//                employee.getPhone(),
//                employee.getAccountStatus().toString(),
//                employee.getRoleId().getName(),
//                managerName,
//                employee.getDesignationId().getName(),
//                employee.getDepartmentId().getName()
//        );
//        } catch (Exception ex) {
//            throw new ApiException(ErrorCode.INTERNAL_ERROR, "Unexpected error occurred while saving employee: " + ex.getMessage());
//        }
//
//    }

    @Transactional
    public EmployeeDetailsDto createEmployee(EmployeeRegisterDto emp, boolean flag) {
        logger.info("Creating employee: {}", emp.getEmail());

        if (employeeRepository.findByEmail(emp.getEmail()).isPresent()) {
            logger.warn("Duplicate employee registration attempt: {}", emp.getEmail());
            throw new DuplicateResourceException("Employee", emp.getEmail());
        }

        logger.debug("Setting department, role, and designation for employee: {}", emp.getEmail());
        Employee employee = new Employee();
        employee.setFName(emp.getFirstName());
        employee.setLName(emp.getLastName());
        employee.setEmail(emp.getEmail());
        employee.setPhone(emp.getPhone());
        employee.setJoinDate(emp.getJoinDate());
        if(flag)
            employee.setAccountStatus(EmployeeStatus.APPROVED);
        else
            employee.setAccountStatus(EmployeeStatus.PENDING);


        Role empRole = roleRepository.findById(emp.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", String.valueOf(emp.getRoleId())));
        employee.setRoleId(empRole);

        Department empDepartment = departmentRepository.findById(emp.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", String.valueOf(emp.getDepartmentId())));
        employee.setDepartmentId(empDepartment);

        Designation empDesignation = designationRepository.findById(emp.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation", String.valueOf(emp.getDesignationId())));
        employee.setDesignationId(empDesignation);

        employee.setPasswordHash(passwordEncoder.encode(emp.getPassword()));

        if (emp.getManagerId() != 0 && !"ADMIN".equals(empRole.getName())) {
            Employee manager = employeeRepository.findById(emp.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", String.valueOf(emp.getManagerId())));

            String managerRole = manager.getRoleId().getName();

            if (managerRole.equals("MANAGER") || managerRole.equals("ADMIN")) {
                employee.setManagerId(manager);
            } else {
                throw new ApiException(ErrorCode.INVALID_INPUT, "Provided Manager ID does not belong to any Existing Manager or Admin Id's ");
            }
        } else if (emp.getManagerId() == 0 && empRole.getName().equals("ADMIN")) {
            employee.setManagerId(null);
        } else if (emp.getManagerId() == 0) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "Manager Id is not provided");
        } else {
            throw new ApiException(ErrorCode.INVALID_INPUT, "ADMIN role cannot have manager");
        }

        try {
            employeeRepository.save(employee);
            logger.info("Employee created successfully: {}", emp.getEmail());
        } catch (DataIntegrityViolationException ex) {
            logger.error("SQL error during employee creation: {}", ex.getMessage(), ex);
            throw new ApiException(ErrorCode.SQL_ERROR);
        } catch (Exception ex) {
            logger.error("Unexpected error during employee creation: {}", ex.getMessage(), ex);
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex);
        }

        String managerName = (employee.getManagerId() != null)
                ? employee.getManagerId().getFullName()
                : "None";

        return new EmployeeDetailsDto(
                employee.getId(),
                employee.getFName(),
                employee.getLName(),
                employee.getEmail(),
                employee.getJoinDate(),
                employee.getPhone(),
                employee.getAccountStatus().toString(),
                employee.getRoleId().getName(),
                managerName,
                employee.getDesignationId().getName(),
                employee.getDepartmentId().getName()
        );
    }

    public List<EmployeeUploadResultDto> bulkCreateEmployees(List<EmployeeRegisterDto> empList) {
        logger.info("Starting bulk employee creation. Count: {}", empList.size());
        List<EmployeeUploadResultDto> result = new ArrayList<>();

        for (EmployeeRegisterDto emp : empList) {
            try {
                EmployeeDetailsDto created = createEmployee(emp, true);
                logger.info("Successfully created employee in bulk: {}", emp.getEmail());
                result.add(EmployeeUploadResultDto.success(created));  // Mark success

            } catch (DuplicateResourceException e) {
                // Add error result without stopping iteration
                logger.warn("Duplicate employee in bulk upload: {}", emp.getEmail());
                result.add(EmployeeUploadResultDto.failure(emp, "Duplicate: " + e.getMessage()));
            } catch (Exception ex) {
                // Log and add generic failure result
                logger.error("Error creating employee {} in bulk upload: {}", emp.getEmail(), ex.getMessage(), ex);
                result.add(EmployeeUploadResultDto.failure(emp, "Error: " + ex.getMessage()));
            }
        }
        logger.info("Bulk employee creation completed.");
        return result;
    }

    public List<EmployeeDetailsDto> getAllEmployee() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(emp -> {
            String managerName = emp.getManagerId() != null ?
                    emp.getManagerId().getFullName() : "None";

            return new EmployeeDetailsDto(
                    emp.getId(),
                    emp.getFName(),
                    emp.getLName(),
                    emp.getEmail(),
                    emp.getJoinDate(),
                    emp.getPhone(),
                    emp.getAccountStatus().name(),
                    emp.getRoleId().getName(),
                    managerName,
                    emp.getDesignationId().getName(),
                    emp.getDepartmentId().getName()
            );
        }).collect(Collectors.toList());
    }

    public EmployeeDetailsDto getEmployeeByEmail(String email) {
        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",email));
        String managerName = emp.getManagerId() != null ?
                emp.getManagerId().getFullName() : "None";
        return new EmployeeDetailsDto(
                emp.getId(),
                emp.getFName(),
                emp.getLName(),
                emp.getEmail(),
                emp.getJoinDate(),
                emp.getPhone(),
                emp.getAccountStatus().toString(),
                emp.getRoleId().getName(),
                managerName,
                emp.getDesignationId().getName(),
                emp.getDepartmentId().getName()
        );
    }

    public EmployeeDetailsDto getEmployeeById(int id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new
                        ResourceNotFoundException("Employee",id));
        String managerName = emp.getManagerId() != null ?
                emp.getManagerId().getFullName() : "None";
        return new EmployeeDetailsDto(
                emp.getId(),
                emp.getFName(),
                emp.getLName(),
                emp.getEmail(),
                emp.getJoinDate(),
                emp.getPhone(),
                emp.getAccountStatus().toString(),
                emp.getRoleId().getName(),
                managerName,
                emp.getDesignationId().getName(),
                emp.getDepartmentId().getName()
        );
    }

    public EmployeeDetailsDto updateEmployee(int id, EmployeeUpdateDto dto) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        emp.setFName(dto.getFName());
        emp.setLName(dto.getLName());
        emp.setPhone(dto.getPhone());
        emp.setRoleId(roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", dto.getRoleId())));
        emp.setDesignationId(designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation", dto.getDesignationId())));
        emp.setDepartmentId(departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId())));

        if(dto.getManagerId() != 0) {
            emp.setManagerId(employeeRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", dto.getManagerId())));
        } else {
            emp.setManagerId(null);
        }

        try {
            employeeRepository.save(emp);
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(ErrorCode.SQL_ERROR);
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex);
        }

        String managerName = emp.getManagerId() != null ?
                emp.getManagerId().getFullName() : "None";

        return new EmployeeDetailsDto(
                emp.getId(),
                emp.getFName(),
                emp.getLName(),
                emp.getEmail(),
                emp.getJoinDate(),
                emp.getPhone(),
                emp.getAccountStatus().toString(),
                emp.getRoleId().getName(),
                managerName,
                emp.getDesignationId().getName(),
                emp.getDepartmentId().getName()
        );
    }

    public void deleteEmployee(int id) {
        logger.info("Deleting employee with ID: {}", id);
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Employee not found for deletion: ID {}", id);
                    return new ResourceNotFoundException("Employee", id);
                });
        try {
            refreshTokenRepository.deleteByEmployeeId(emp);
            employeeRepository.delete(emp);
            logger.info("Employee deleted successfully: ID {}", id);
        } catch (DataIntegrityViolationException e) {
            logger.error("SQL error while deleting employee ID {}: {}", id, e.getMessage(), e);
            throw new ApiException(ErrorCode.SQL_ERROR, e.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error during employee deletion ID {}: {}", id, ex.getMessage(), ex);
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }
    }

    public void updatePassword(PasswordUpdateDto dto) {
        String email = SecurityUtil.getCurrentUserEmail();
        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new
                        ResourceNotFoundException("Employee",email));
        if(!dto.getNewPassword().contentEquals(dto.getConfirmPassword()))
            throw new ApiException(ErrorCode.INVALID_INPUT, "New password and Confirm Password does not match");
        if(!passwordEncoder.matches(dto.getCurrentPassword(),emp.getPasswordHash())) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "Current password is incorrect");
        }
        emp.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));

        try {
            employeeRepository.save(emp);
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(ErrorCode.SQL_ERROR);
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex);
        }
    }

    public EmployeeDetailsDto getCurrentEmployee() {
        String email =  SecurityUtil.getCurrentUserEmail();
        return getEmployeeByEmail(email);
    }

    public EmployeeMetadataDto getEmployeeMetadata() {
        String email = SecurityUtil.getCurrentUserEmail();
        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",email));
        int managerId = (emp.getManagerId() != null)
                ? emp.getManagerId().getId()
                : 0;
        return new EmployeeMetadataDto(
                emp.getId(),
                emp.getFullName(),
                emp.getEmail(),
                emp.getJoinDate(),
                emp.getPhone(),
                emp.getRoleId().getName(),
                managerId,
                emp.getDesignationId().getId(),
                emp.getDepartmentId().getId()
        );
    }

    public void rejectAccountStatus(int id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",id));
        emp.setAccountStatus(EmployeeStatus.REJECTED);
        try {
            employeeRepository.save(emp);
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(ErrorCode.SQL_ERROR);
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex);
        }
    }

    public void approveAccountStatus(int id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new
                        ResourceNotFoundException("Employee",id));
        emp.setAccountStatus(EmployeeStatus.APPROVED);
        try {
            employeeRepository.save(emp);
            System.out.println("Running leave client");
            leaveClient.initializeLeaveBalance(emp.getId(), emp.getJoinDate());
            System.out.println("left leave client");
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(ErrorCode.SQL_ERROR);
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }
    }

    public List<EmployeeMetadataDto> getAllApprovedEmployees() {
        List<Employee> employees = employeeRepository.findAllByAccountStatus(EmployeeStatus.APPROVED);

        return employees.stream().map(emp -> {
            int managerId = (emp.getManagerId() != null)
                    ? emp.getManagerId().getId()
                    : 0;

            return new EmployeeMetadataDto(
                    emp.getId(),
                    emp.getFullName(),
                    emp.getEmail(),
                    emp.getJoinDate(),
                    emp.getPhone(),
                    emp.getRoleId().getName(),
                    managerId,
                    emp.getDesignationId().getId(),
                    emp.getDepartmentId().getId()
            );
        }).collect(Collectors.toList());
    }

    public List<EmployeeMetadataDto> getTeamMembers(int mgrId) {
        List<Employee> employees = employeeRepository
                .findAllByManagerId(employeeRepository.findById(mgrId)
                        .orElseThrow(() -> new ResourceNotFoundException("Manager from Employee", mgrId))
        );

        return employees.stream().map(emp -> {
            int managerId = (emp.getManagerId() != null)
                    ? emp.getManagerId().getId()
                    : 0;

            return new EmployeeMetadataDto(
                    emp.getId(),
                    emp.getFullName(),
                    emp.getEmail(),
                    emp.getJoinDate(),
                    emp.getPhone(),
                    emp.getRoleId().getName(),
                    managerId,
                    emp.getDesignationId().getId(),
                    emp.getDepartmentId().getId()
            );
        }).collect(Collectors.toList());
    }

    public String getEmployeeNameById(int id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",id));
        return emp.getFullName();
    }

    public boolean existsByEmployeeId(int id) {
        return employeeRepository.existsById(id);
    }

    public List<EmployeeMetadataDto> getAllAdminTeamMembers() {
        Role adminRole = roleRepository.findById(1)
                .orElseThrow(() -> new ResourceNotFoundException("Role", 1));

        List<Employee> employees = employeeRepository.findAllByRoleIdNot( adminRole );

        return employees.stream().map(emp -> {
            int managerId = (emp.getManagerId() != null)
                    ? emp.getManagerId().getId()
                    : 0;

            return new EmployeeMetadataDto(
                    emp.getId(),
                    emp.getFullName(),
                    emp.getEmail(),
                    emp.getJoinDate(),
                    emp.getPhone(),
                    emp.getRoleId().getName(),
                    managerId,
                    emp.getDesignationId().getId(),
                    emp.getDepartmentId().getId()
            );
        }).collect(Collectors.toList());
    }
}
