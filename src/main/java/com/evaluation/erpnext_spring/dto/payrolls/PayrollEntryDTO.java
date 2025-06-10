package com.evaluation.erpnext_spring.dto.payrolls;


import java.util.List;

import lombok.Data;

@Data
public class PayrollEntryDTO {
    private String company;
    private String posting_date;
    private String currency;
    private String payroll_frequency;
    private String start_date;
    private String end_date;
    private List<PayrollEmployeeDetail> employees;
    private boolean draft;
}
