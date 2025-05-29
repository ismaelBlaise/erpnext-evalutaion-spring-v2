package com.evaluation.erpnext_spring.dto.purchase_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PcoMessage {
    @JsonProperty("message")
    private PcoListResponse message;
}
