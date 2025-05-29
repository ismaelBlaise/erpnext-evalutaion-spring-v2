package com.evaluation.erpnext_spring.dto.supplier_quotations;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpqDto {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("transaction_date")
    private String transactionDate;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("supplier")
    private String supplier;
    
    @JsonProperty("grand_total")
    private BigDecimal grandTotal;
    
    @JsonProperty("creation")
    private String creation;
    
    @JsonProperty("modified")
    private String modified;
    
    @JsonProperty("currency")
    private String currency;
    
   
}
