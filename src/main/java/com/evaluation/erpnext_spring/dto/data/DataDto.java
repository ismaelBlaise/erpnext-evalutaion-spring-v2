package com.evaluation.erpnext_spring.dto.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DataDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("salary_component_abbr")
    private String abbr;

    @JsonProperty("default_currency")
    private String currency;
}
