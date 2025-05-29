package com.evaluation.erpnext_spring.dto.company;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyListResponseDTO {
    @JsonProperty("data")
    List<CompanyDTO> data;
}
