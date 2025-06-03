package com.evaluation.erpnext_spring.dto.imports;

import java.time.LocalDate;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class SalaireData {
    
    @CsvBindByName(column = "Mois")
    private LocalDate mois;    
    @CsvBindByName(column = "Ref Employe")
    private String refEmploye;  
    @CsvBindByName(column = "Salaire Base")
    private Double salaireBase;       
    @CsvBindByName(column = "Salaire")
    private String salaryStructure;   
    
}