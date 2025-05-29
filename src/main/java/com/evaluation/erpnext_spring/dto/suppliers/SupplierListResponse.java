package com.evaluation.erpnext_spring.dto.suppliers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SupplierListResponse {
    @JsonProperty("data")
    private List<SupplierDto> data;
    @JsonIgnore
    private int total;

    public SupplierListResponse() {}

    public SupplierListResponse(List<SupplierDto> data) {
        this.data = data;
    }

   
}