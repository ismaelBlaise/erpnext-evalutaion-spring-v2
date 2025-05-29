package com.evaluation.erpnext_spring.dto.supplier_quotations;

import java.util.List;

import com.evaluation.erpnext_spring.dto.paginations.PaginationDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpqListResponse {
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("data")
    private List<SpqDto> data;
    
    @JsonProperty("message_inf")
    private String successMessage;
    
    @JsonProperty("pagination")
    private PaginationDto pagination;
}
