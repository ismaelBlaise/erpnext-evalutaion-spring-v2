package com.evaluation.erpnext_spring.dto.quotations;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SupplierQuotationGroupDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("status")
    private String status;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("grand_total")
    private Double grandTotal;

    @JsonProperty("items")
    private List<SupplierQuotationItemDto> items;
}
