package com.evaluation.erpnext_spring.dto.salaries;

import java.util.ArrayList;
import java.util.List;
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
        for (int i = 0; i < dataDtos.size(); i++) {
                componentsSum.add(0.0);
        }

             
        
        for (SalarySlipDto salarySlipDto : salarySlipDtos) {
            totalGrossPay+=salarySlipDto.getGrossPay();
            totalDeductions+=salarySlipDto.getTotalDeduction();
            totalNetPay+=salarySlipDto.getNetPay();
            this.currency=salarySlipDto.getCurrency();
            
            
            List<Double> comps = salarySlipDto.getComponentsDef();
            if (comps != null && comps.size() == dataDtos.size()) {
                for (int i = 0; i < comps.size(); i++) {
                    componentsSum.set(i, componentsSum.get(i) + comps.get(i));
                }
            }
            

        }
    }

    
}