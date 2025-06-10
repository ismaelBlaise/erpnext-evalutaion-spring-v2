package com.evaluation.erpnext_spring.dto.structures;

import lombok.Data;

@Data
public class StructureAssignement {
    private String company;
    private String salary_structure;
    private String currency;
    private String employee;
    private String from_date;
    private Double base;
    private Double variable;
    
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
