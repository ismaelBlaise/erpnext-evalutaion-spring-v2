package com.evaluation.erpnext_spring.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyDTO {
    @JsonProperty("name")
    String name;
}
