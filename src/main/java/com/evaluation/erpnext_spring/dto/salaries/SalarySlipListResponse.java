package com.evaluation.erpnext_spring.dto.salaries;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalarySlipListResponse {
    @JsonProperty("data")
    List<SalarySlipDto> data;
}
