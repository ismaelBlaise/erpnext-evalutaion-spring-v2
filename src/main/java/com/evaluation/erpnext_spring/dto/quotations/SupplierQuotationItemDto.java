package com.evaluation.erpnext_spring.dto.quotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SupplierQuotationItemDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("qty")
    private Double qty;

    @JsonProperty("rate")
    private Double rate;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("warehouse")
    private String warehouse;
}
