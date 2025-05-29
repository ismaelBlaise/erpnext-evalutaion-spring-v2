package com.evaluation.erpnext_spring.dto.requests_for_quotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RfqDto {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("transaction_date")
    private String transactionDate;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("schedule_date")
    private String scheduleDate;
    
    @JsonProperty("message_for_supplier")
    private String messageForSupplier;
    
    @JsonProperty("creation")
    private String creation;
    
    @JsonProperty("modified")
    private String modified;
    
    @JsonProperty("quote_status")
    private String quoteStatus;
    
    @JsonProperty("email_sent")
    private Integer emailSent;

    
}