package com.evaluation.erpnext_spring.dto.salaries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.evaluation.erpnext_spring.dto.data.DataDto;

import lombok.Data;

@Data
public class SalaryTotalsResponse {
    private double totalGrossPay;
    private double totalDeductions;
    private double totalNetPay;
    private String currency;

    private List<Double> componentsSum=new ArrayList<>();
    public SalaryTotalsResponse(List<SalarySlipDto> salarySlipDtos,List<DataDto> dataDtos) {
        for (SalarySlipDto salarySlipDto : salarySlipDtos) {
            totalGrossPay+=salarySlipDto.getGrossPay();
            totalDeductions+=salarySlipDto.getTotalDeduction();
            totalNetPay+=salarySlipDto.getNetPay();
            this.currency=salarySlipDto.getCurrency();
            
            for (int i =0;i<dataDtos.size() ;i++) {
                
                
            }

        }
    }

    
}