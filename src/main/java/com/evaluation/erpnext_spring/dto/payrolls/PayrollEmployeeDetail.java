package com.evaluation.erpnext_spring.dto.payrolls;

import lombok.Data;

@Data
public class PayrollEmployeeDetail {
    private String employee;
    private String is_salary_withheld;
}