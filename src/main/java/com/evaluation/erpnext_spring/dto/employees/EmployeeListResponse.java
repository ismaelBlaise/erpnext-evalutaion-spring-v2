package com.evaluation.erpnext_spring.dto.employees;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeeListResponse {
    @JsonProperty("data")
    private List<EmployeeDto> data;
    @JsonIgnore
    private int total;

    public EmployeeListResponse() {}

    public EmployeeListResponse(List<EmployeeDto> data) {
        this.data = data;
    }
}