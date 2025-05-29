package com.evaluation.erpnext_spring.dto.supplier_quotations;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpqMessage {
    @JsonProperty("message")
    private SpqListResponse message;
}
