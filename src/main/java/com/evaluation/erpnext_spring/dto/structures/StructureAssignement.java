package com.evaluation.erpnext_spring.dto.structures;

import lombok.Data;

@Data
public class StructureAssignement {
    private String name;
    private String company;
    private String salary_structure;
    private String currency;
    private String employee;
    private String employee_name; 
    private String from_date;
    private String to_date;
    private Double base;
    private Double variable;
    private Double total;  
    private String pay_frequency;  
    private String department;
    private String cost_center;
    private String branch;  
    private Boolean is_active;
    private String created_by;
    private String creation_date;
    private String modified_by;
    private String modified_date;
    private String notes;  

    
    public StructureAssignement(
            String company,
            String salary_structure,
            String currency,
            String employee,
            String from_date,
            Double base,
            Double variable,
            String pay_frequency,
            String department,
            String cost_center) {
        this.company = company;
        this.salary_structure = salary_structure;
        this.currency = currency;
        this.employee = employee;
        this.from_date = from_date;
        this.base = base;
        this.variable = variable;
        this.pay_frequency = pay_frequency;
        this.department = department;
        this.cost_center = cost_center;
        
        this.is_active = true; 
    }

    
    public StructureAssignement() {}

    
    public StructureAssignement(String company, String salary_structure, String currency, String employee,
            String from_date, Double base, Double variable) {
        this.company = company;
        this.salary_structure = salary_structure;
        this.currency = currency;
        this.employee = employee;
        this.from_date = from_date;
        this.base = base;
        this.variable = variable;
    }
}
