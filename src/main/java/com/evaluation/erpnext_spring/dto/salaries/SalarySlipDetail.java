package com.evaluation.erpnext_spring.dto.salaries;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalarySlipDetail {
    @JsonProperty("data")
    SalarySlipDto data;
}
