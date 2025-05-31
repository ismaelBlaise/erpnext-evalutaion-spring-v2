package com.evaluation.erpnext_spring.dto.employees;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeFilter {
    private String employeeName;
    private String position;
    private String department;
    private String company;
    private String hireDateStart;
    private String hireDateEnd;
}
