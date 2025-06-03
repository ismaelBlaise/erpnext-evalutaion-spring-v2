package com.evaluation.erpnext_spring.dto.imports;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class EmployeData {
    @CsvBindByName(column = "Ref")
    private String ref;
    @CsvBindByName(column = "Nom")
    private String nom;
    @CsvBindByName(column = "Prenom")
    private String prenom;
    @CsvBindByName(column = "genre")
    private String genre;
    @CsvBindByName(column = "Date embauche")
    private String dateEmbauche;
    @CsvBindByName(column = "date naissance")
    private String dateNaissance;
    @CsvBindByName(column = "company")
    private String company;
   
}

    