package com.evaluation.erpnext_spring.dto.payrolls;


import java.util.List;

import lombok.Data;

@Data
public class PayrollEntryDTO {
    private String company;
    private String posting_date;
    private String currency;
    private String payroll_frequency;
    private Double exchange_rate=1.0;
    private String payroll_payable_account;
    private String cost_center;
    private String start_date;
    private String end_date;
    private List<PayrollEmployeeDetail> employees;
    private boolean draft;
}
