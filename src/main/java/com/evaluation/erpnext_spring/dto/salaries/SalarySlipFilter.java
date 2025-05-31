package com.evaluation.erpnext_spring.dto.salaries;

import lombok.Data;

@Data
public class SalarySlipFilter {
    private String employee;
    private String startDate;  // format ISO "yyyy-MM-dd"
    private String endDate;    // format ISO "yyyy-MM-dd"
}
