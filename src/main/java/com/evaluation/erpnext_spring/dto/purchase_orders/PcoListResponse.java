package com.evaluation.erpnext_spring.dto.purchase_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;

@Data
public class PcoListResponse {
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("data")
    private List<PcoDto> data;
    
    @JsonProperty("count")
    private Integer count;
    
    // @JsonProperty("total_count")
    // private Integer totalCount;
    
    // @JsonProperty("page")
    // private Integer page;
    
    // @JsonProperty("page_size")
    // private Integer pageSize;
    
    // @JsonProperty("total_pages")
    // private Integer totalPages;
    
    @JsonProperty("message")
    private String message;

}