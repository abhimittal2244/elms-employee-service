package com.elms.employee_service.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name="LEAVE-SERVICE")
public interface LeaveClient {

    @PostMapping("/leave/leaveBalance/init1")
    public void initializeLeaveBalance(@RequestParam int employeeId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate joinDate);
}
