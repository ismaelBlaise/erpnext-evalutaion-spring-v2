package com.evaluation.erpnext_spring.dto.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DataListReponse {
    @JsonProperty("data")
    private List<DataDto> data;
}
