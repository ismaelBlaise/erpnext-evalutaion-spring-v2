package com.evaluation.erpnext_spring.dto.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class SalaireData {
    @JsonProperty("mois")
    @CsvBindByName(column = "Mois")
    private String mois; 
    @JsonProperty("ref_employe")
    @CsvBindByName(column = "Ref Employe")
    private String refEmploye;  
    @JsonProperty("salaire_base")
    @CsvBindByName(column = "Salaire Base")
    private Double salaireBase; 
    @JsonProperty("salary_structure")
    @CsvBindByName(column = "Salaire")
    private String salaryStructure;   
    
}