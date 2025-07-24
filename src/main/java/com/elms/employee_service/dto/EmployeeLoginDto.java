package com.elms.employee_service.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EmployeeLoginDto {
    private String email;
    private String password;

//    public EmployeeLoginDto() {
//    }

//    public String getEmail() {
//        return username;
//    }
//
//    public void setEmail(String email) {
//        this.username = email;
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
//    @Override
//    public String toString() {
//        return "EmployeeLoginDto{" +
//                "email='" + username + '\'' +
//                ", password='" + password + '\'' +
//                '}';
//    }
}
