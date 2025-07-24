package com.elms.employee_service.util;

import com.elms.employee_service.dto.EmployeeRegisterDto;
import com.elms.employee_service.dto.EmployeeUploadResultDto;
import com.elms.employee_service.dto.ParsedEmployeeSheet;
import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.enums.ErrorCode;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ExcelHelper {

    public ParsedEmployeeSheet parseEmployeeFile(MultipartFile file) throws IOException {
        List<EmployeeRegisterDto> employeeList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Row header = sheet.getRow(0);
        int errorColIndex = header.getLastCellNum();
        header.createCell(errorColIndex).setCellValue("Errors");

        ParsedEmployeeSheet parsed = new ParsedEmployeeSheet();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            parsed.getRowMap().put(i,row);

            EmployeeRegisterDto emp = new EmployeeRegisterDto();
            StringBuilder errorLog = new StringBuilder();

            try {
                emp.setFirstName(getStringCell(row.getCell(0), "First Name", errorLog));
                emp.setLastName(getStringCell(row.getCell(1), "Last Name", errorLog));
                emp.setEmail(getStringCell(row.getCell(2), "Email", errorLog));
                emp.setPhone(getPhone(row.getCell(3), errorLog));
                emp.setJoinDate(getJoinDate(row.getCell(4), errorLog));
                emp.setPassword(getStringCell(row.getCell(5), "Password", errorLog));
                emp.setRoleId(getInteger(row.getCell(6), "Role ID", errorLog));
                emp.setManagerId(getInteger(row.getCell(7), "Manager ID", errorLog));
                emp.setDepartmentId(getInteger(row.getCell(8), "Department ID", errorLog));
                emp.setDesignationId(getInteger(row.getCell(9), "Designation ID", errorLog));
            } catch (Exception ex) {
                errorLog.append("Unexpected error: ").append(ex.getMessage());
            }

            if (!errorLog.isEmpty()) {
                row.createCell(errorColIndex).setCellValue(errorLog.toString());
            } else {
                employeeList.add(emp);
            }
        }

        File uploadsDir = new File("uploads");
        if (!uploadsDir.exists()) uploadsDir.mkdirs();

        String filePath = uploadsDir.getAbsolutePath() + "/employee_upload_result.xlsx";
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        parsed.setEmployeeList(employeeList);
        parsed.setWorkbook(workbook);
        parsed.setUpdatedFilePath(filePath);
        return parsed;
    }

    private String getStringCell(Cell cell, String fieldName, StringBuilder errors) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errors.append(fieldName).append(" is empty. ");
            return "";
        }
        return cell.getStringCellValue().trim();
    }

    private String getPhone(Cell cell, StringBuilder errors) {
        if (cell == null) {
            errors.append("Phone is empty. ");
            return "";
        }
        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
                default -> cell.toString();
            };
        } catch (Exception e) {
            errors.append("Invalid phone format. ");
            return "";
        }
    }

    private LocalDate getJoinDate(Cell cell, StringBuilder errors) {
        if (!DateUtil.isCellDateFormatted(cell)) {
            errors.append("Invalid or missing Join Date. ");
            return null;
        }
        return cell.getDateCellValue().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private int getInteger(Cell cell, String fieldName, StringBuilder errors) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errors.append(fieldName).append(" is empty. ");
            return 0;
        }
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> (int) cell.getNumericCellValue();
                case STRING -> Integer.parseInt(cell.getStringCellValue().trim());
                default -> {
                    errors.append("Invalid format for ").append(fieldName).append(". ");
                    yield 0;
                }
            };
        } catch (Exception e) {
            errors.append("Error parsing ").append(fieldName).append(". ");
            return 0;
        }
    }

    public void attachUploadResults(Map<Integer, Row> rowMap, List<EmployeeUploadResultDto> results, int errorColIndex) {
    try{
        for (int i = 0; i < results.size(); i++) {
            EmployeeUploadResultDto res = results.get(i);
            Row row = rowMap.get(i+1); // +1 to skip header
            if (row == null) continue;
            Cell cell = row.createCell(errorColIndex);
            if (res.isSuccess()) {
                cell.setCellValue("Success");
            } else {
                cell.setCellValue(res.getError());
            }
        }
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex);
        }
    }

    public void writeWorkbook(Workbook workbook, String outputPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputPath);
        workbook.write(fos);
        workbook.close();
        fos.close();
    }

}


