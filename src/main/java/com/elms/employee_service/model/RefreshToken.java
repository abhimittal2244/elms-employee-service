package com.elms.employee_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;
    private Date expiry;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employeeId;

//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public Date getExpiry() {
//        return expiry;
//    }
//
//    public void setExpiry(Date expiry) {
//        this.expiry = expiry;
//    }
}
