package com.evaluation.erpnext_spring.dto.requests_for_quotation;

import com.evaluation.erpnext_spring.dto.paginations.PaginationDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;


@Data
public class RfqListResponse {
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("data")
    private List<RfqDto> data;
    
    @JsonProperty("message_inf")
    private String successMessage;
    
    @JsonProperty("pagination")
    private PaginationDto pagination;

   
}