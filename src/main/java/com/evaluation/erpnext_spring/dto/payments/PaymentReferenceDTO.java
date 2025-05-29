package com.evaluation.erpnext_spring.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentReferenceDTO {
    @JsonProperty("reference_doctype")
    private String referenceDoctype = "Purchase Invoice";
    
    @JsonProperty("reference_name")
    private String referenceName;
    
    @JsonProperty("allocated_amount")
    private BigDecimal allocatedAmount;
    
    @JsonProperty("outstanding_amount")
    private BigDecimal outstandingAmount;
    
    @JsonProperty("due_date")
    private String dueDate;
    
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
}