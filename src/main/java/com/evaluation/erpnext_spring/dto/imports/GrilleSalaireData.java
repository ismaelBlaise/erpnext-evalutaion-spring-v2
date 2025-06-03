package com.evaluation.erpnext_spring.dto.imports;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class GrilleSalaireData {
    @CsvBindByName(column = "salary structure")
    private String salaryStructure;
    @CsvBindByName(column = "name")
    private String name;     
    @CsvBindByName(column = "Abbr")
    private String abbr;      
    @CsvBindByName(column = "type")
    private String type;     
    @CsvBindByName(column = "valeur")        
    private String valeur;      
    @CsvBindByName(column = "company")     
    private String company;          
}