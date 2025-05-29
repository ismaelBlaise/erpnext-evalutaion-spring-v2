package com.evaluation.erpnext_spring.dto.quotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SupplierQuotationDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("creation")
    private String creation;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("quotation_number")
    private String quotationNumber;

    @JsonProperty("total")
    private Double total;

    @JsonProperty("grand_total")
    private Double grandTotal;

    @JsonProperty("status")
    private String status;

    @JsonProperty("transaction_date")
    private String transactionDate;

}
