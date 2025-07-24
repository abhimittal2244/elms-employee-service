package com.elms.employee_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
//import lombok.Data;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeRegisterDto {

    @NotBlank(message = "First Name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last Name cannot be empty")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = "Password must contain upper case, lower case, a number, and a special character"
    )
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[+]?[0-9]{1,4}[\\s-]?[0-9]{6,12}$",
            message = "Invalid phone number format"
    )
    private String phone;
    private LocalDate joinDate;

    @NotBlank(message = "Role Id is required")
    private int roleId;

    private int managerId;

    @NotBlank(message = "Designation Id is required")
    private int designationId;

    @NotBlank(message = "Department Id is required")
    private int departmentId;

//    @Override
//    public String toString() {
//        return "EmployeeRegisterDto{" +
//                "name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", password='" + password + '\'' +
//                ", phone='" + phone + '\'' +
//                ", joinDate=" + joinDate +
//                ", roleId=" + roleId +
//                ", managerId=" + managerId +
//                ", designationId=" + designationId +
//                ", departmentId=" + departmentId +
//                '}';
//    }

//    public EmployeeRegisterDto() {
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public Date getJoinDate() {
//        return joinDate;
//    }
//
//    public void setJoinDate(Date joinDate) {
//        this.joinDate = joinDate;
//    }
//
//    public int getRoleId() {
//        return roleId;
//    }
//
//    public void setRoleId(int roleId) {
//        this.roleId = roleId;
//    }
//
//    public int getManagerId() {
//        return managerId;
//    }
//
//    public void setManagerId(int managerId) {
//        this.managerId = managerId;
//    }
//
//    public int getDesignationId() {
//        return designationId;
//    }
//
//    public void setDesignationId(int designationId) {
//        this.designationId = designationId;
//    }
//
//    public int getDepartmentId() {
//        return departmentId;
//    }
//
//    public void setDepartmentId(int departmentId) {
//        this.departmentId = departmentId;
//    }
}
