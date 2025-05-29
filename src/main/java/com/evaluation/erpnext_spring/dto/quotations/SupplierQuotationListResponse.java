package com.evaluation.erpnext_spring.dto.quotations;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SupplierQuotationListResponse {

    @JsonProperty("data")
    private List<SupplierQuotationDto> data;

    public List<SupplierQuotationDto> getData() {
        return data;
    }

    public void setData(List<SupplierQuotationDto> data) {
        this.data = data;
    }
}


