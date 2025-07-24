package com.elms.employee_service.controller;

import com.elms.employee_service.dto.*;
import com.elms.employee_service.service.EmployeeService;
import com.elms.employee_service.util.ExcelHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/employee/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ExcelHelper excelHelper;

    @GetMapping("/test")
    public String test() {
        return "Hello from Employee Controller";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<EmployeeDetailsDto>> getAllEmployee() {
        return ResponseEntity.ok(employeeService.getAllEmployee());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllApprovedEmployees")
    public ResponseEntity<List<EmployeeMetadataDto>> getAllApprovedEmployees() {
        return ResponseEntity.ok(employeeService.getAllApprovedEmployees());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/email/{email}")
    public ResponseEntity<EmployeeDetailsDto> getEmployeeByEmail(@PathVariable String email) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetailsDto> getEmployeeById(@PathVariable int id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/name/{id}")
    public ResponseEntity<String> getEmployeeNameById(@PathVariable int id) {
        return ResponseEntity.ok(employeeService.getEmployeeNameById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/me")
    public ResponseEntity<EmployeeDetailsDto> getCurrentEmployee() {
        return ResponseEntity.ok(employeeService.getCurrentEmployee());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/me/id")
    public ResponseEntity<Integer> getCurrentEmployeeId() {
        return ResponseEntity.ok(employeeService.getCurrentEmployee().getEmployeeId());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/getEmployeeMetadata")
    public ResponseEntity<EmployeeMetadataDto> getEmployeeMetadata() {
        return ResponseEntity.ok(employeeService.getEmployeeMetadata());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsByEmployeeId (@PathVariable int id) {
        return ResponseEntity.ok(employeeService.existsByEmployeeId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDetailsDto> updateEmployee(@PathVariable int id, @RequestBody EmployeeUpdateDto dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @PutMapping
    @RequestMapping("/updatePassword")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdateDto dto) {
        employeeService.updatePassword(dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveAccountStatus(@PathVariable int id) {
        employeeService.approveAccountStatus(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectAccountStatus(@PathVariable int id) {
        employeeService.rejectAccountStatus(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/employees")
    public ResponseEntity<List<EmployeeMetadataDto>> getTeamMembers(@PathVariable int managerId){
        return ResponseEntity.ok(employeeService.getTeamMembers(managerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/employees")
    public ResponseEntity<List<EmployeeMetadataDto>> getAllAdminTeamMembers(){
        return ResponseEntity.ok(employeeService.getAllAdminTeamMembers());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-employees")
    public ResponseEntity<?> uploadEmployees(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid file.");
        }
        ParsedEmployeeSheet parsed = excelHelper.parseEmployeeFile(file);

        List<EmployeeUploadResultDto> results = employeeService.bulkCreateEmployees(parsed.getEmployeeList());
        excelHelper.attachUploadResults(parsed.getRowMap(), results, parsed.getErrorColIndex());

        String outputFilePath = "uploads/employee_upload_result.xlsx";
        excelHelper.writeWorkbook(parsed.getWorkbook(), outputFilePath);

        return ResponseEntity.ok(results);


    }

    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        String[] headers = {
                "First Name", "Last Name", "Email", "Phone", "Join Date", "Password",
                "Role ID", "Manager ID", "Department ID", "Designation ID"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
