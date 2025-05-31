package com.evaluation.erpnext_spring.dto.employees;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeeDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("employee_name")
    private String employeeName;
    @JsonProperty("department")
    private String department;
    @JsonProperty("designation")
    private String designation;
    @JsonProperty("company")
    private String company;

    public EmployeeDto() {}

    public EmployeeDto(String name, String employeeName, String department, String designation, String company) {
        this.name = name;
        this.employeeName = employeeName;
        this.department = department;
        this.designation = designation;
        this.company = company;
    }
}