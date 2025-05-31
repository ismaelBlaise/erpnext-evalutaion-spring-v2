package com.evaluation.erpnext_spring.dto.employees;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeDto {
    @JsonProperty("name")
    private String name; 
    
    @JsonProperty("employee_name")
    private String employeeName; 

    @JsonProperty("first_name")
    private String firstName; 

    @JsonProperty("last_name")
    private String lastName; 

    @JsonProperty("status")
    private String status; 

    @JsonProperty("company")
    private String company; 

    @JsonProperty("department")
    private String department;  
    
    @JsonProperty("designation")
    private String position;  
    
    @JsonProperty("gender")
    private String gender;  
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_joining")
    private LocalDate hireDate;  
    
    @JsonProperty("employment_type")
    private String employmentType;  
    
    @JsonProperty("branch")
    private String branch;  
    
    @JsonProperty("company_email")
    private String email;  

    // Constructeurs
    public EmployeeDto() {}

    
}