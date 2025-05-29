package com.evaluation.erpnext_spring.dto.orders;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class PurchaseOrderListResponse {

    @JsonProperty("data")
    private List<PurchaseOrderDTO> data;
}
