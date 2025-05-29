package com.evaluation.erpnext_spring.dto.payments;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentResponseGroupDTO {
    @JsonProperty("data")
    private PaymentResponseDTO data;
}
