package com.evaluation.erpnext_spring.dto.grilles;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalaryComponentDto {
    @JsonProperty("salary_component")
    private String componentName;
    
    @JsonProperty("abbr")
    private String abbreviation;
    
    @JsonProperty("amount")
    private Double amount;
    
    @JsonProperty("depends_on_payment_days")
    private Boolean dependsOnPaymentDays;
    
    @JsonProperty("is_tax_applicable")
    private Boolean isTaxable;
    
    @JsonProperty("amount_based_on_formula")
    private Boolean amountBasedOnFormula;
    
    @JsonProperty("formula")
    private String formula;
    
    @JsonProperty("type")
    private String type; 
}