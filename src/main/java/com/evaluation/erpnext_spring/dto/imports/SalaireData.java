package com.evaluation.erpnext_spring.dto.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class SalaireData {
    @JsonProperty("mois")
    
    // @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "Mois")
    private String mois; 

    // @CsvBindByPosition(position = 0)
    @JsonProperty("ref_employe")
    @CsvBindByName(column = "Ref Employe")
    private String refEmploye;  
    
    // @CsvBindByPosition(position = 0)
    @JsonProperty("salaire_base")
    @CsvBindByName(column = "Salaire Base")
    private Double salaireBase; 
    
    // @CsvBindByPosition(position = 0)
    @JsonProperty("salary_structure")
    @CsvBindByName(column = "Salaire")
    private String salaryStructure;   
    
}