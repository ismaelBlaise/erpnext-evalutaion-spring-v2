package com.evaluation.erpnext_spring.dto.auth;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ERPNextAuthResponse {
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("home_page")
    private String homePage;
    
    @JsonProperty("full_name")
    private String fullName;
    
    @JsonProperty("sid")
    private String sid;

   
}