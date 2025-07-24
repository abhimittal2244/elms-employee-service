package com.elms.employee_service.model;

import com.elms.employee_service.enums.EmployeeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "First Name cannot be empty")
    @Column(nullable = false)
    private String fName;

    @NotBlank(message = "Last Name cannot be empty")
    @Column(nullable = false)
    private String lName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeStatus accountStatus;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private LocalDate joinDate;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role roleId;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee managerId;

    @ManyToOne
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designationId;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department departmentId;

    public String getFullName() {
        return fName + " " + lName;
    }
}
