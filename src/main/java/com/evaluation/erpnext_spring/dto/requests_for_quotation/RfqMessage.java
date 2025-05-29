package com.evaluation.erpnext_spring.dto.requests_for_quotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RfqMessage {
    @JsonProperty("message")
    private RfqListResponse message;
}
