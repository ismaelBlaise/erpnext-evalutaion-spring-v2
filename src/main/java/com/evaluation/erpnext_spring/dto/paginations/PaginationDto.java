package com.evaluation.erpnext_spring.dto.paginations;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class PaginationDto {
    @JsonProperty("page")
    private Integer page;
    
    @JsonProperty("page_size")
    private Integer pageSize;
    
    @JsonProperty("total")
    private Integer total;

    
    
}