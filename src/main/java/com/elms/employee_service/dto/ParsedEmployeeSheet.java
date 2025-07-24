package com.elms.employee_service.dto;

import lombok.Data;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Getter
public class ParsedEmployeeSheet {
    private List<EmployeeRegisterDto> employeeList;
    private Map<Integer, Row> rowMap;
    private Workbook workbook;
    private int errorColIndex=10;
    private String updatedFilePath;

    public ParsedEmployeeSheet() {
        this.rowMap = new HashMap<>();
    }
}
