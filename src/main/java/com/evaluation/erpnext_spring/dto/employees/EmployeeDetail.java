package com.evaluation.erpnext_spring.dto.employees;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmployeeDetail {
    @JsonProperty("data")
    private EmployeeDto data;
}
