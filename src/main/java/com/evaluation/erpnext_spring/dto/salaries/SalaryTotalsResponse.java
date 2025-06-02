package com.evaluation.erpnext_spring.dto.salaries;

import java.util.List;

import lombok.Data;

@Data
public class SalaryTotalsResponse {
    private double totalGrossPay;
    private double totalDeductions;
    private double totalNetPay;
    private String currency;
    public SalaryTotalsResponse(List<SalarySlipDto> salarySlipDtos) {
        for (SalarySlipDto salarySlipDto : salarySlipDtos) {
            totalGrossPay+=salarySlipDto.getGrossPay();
            totalDeductions+=salarySlipDto.getTotalDeduction();
            totalNetPay+=salarySlipDto.getNetPay();
            this.currency=salarySlipDto.getCurrency();

        }
    }

    
}