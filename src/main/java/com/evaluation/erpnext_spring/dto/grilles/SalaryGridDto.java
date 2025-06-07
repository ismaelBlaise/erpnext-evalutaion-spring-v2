package com.evaluation.erpnext_spring.dto.grilles;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalaryGridDto {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("employee")
    private String employee;
    
    @JsonProperty("company")
    private String company;

    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("from_date")
    private String fromDate;
    
    @JsonProperty("to_date")
    private String toDate;

    @JsonProperty("payment_frequency")
    private String paymentFrequency;  

    @JsonProperty("earnings")
    private List<SalaryComponentDto> earnings;
    
    @JsonProperty("deductions")
    private List<SalaryComponentDto> deductions;
    
    @JsonProperty("is_active")
    private String isActive;

     public void addEarning(SalaryComponentDto earning) {
        this.earnings.add(earning);
    }
    
    public void addDeduction(SalaryComponentDto deduction) {
        this.deductions.add(deduction);
    }
}