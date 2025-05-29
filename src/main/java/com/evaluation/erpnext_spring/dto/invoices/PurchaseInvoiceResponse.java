package com.evaluation.erpnext_spring.dto.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseInvoiceResponse {
    @JsonProperty("data")
    private PurchaseInvoice data;

    }