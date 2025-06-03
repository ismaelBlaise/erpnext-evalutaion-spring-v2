package com.evaluation.erpnext_spring.dto.imports;

import lombok.Data;

import java.util.List;

@Data
public class ResultatImport {
    private List<EmployeData> employesValides;
    private List<GrilleSalaireData> grilleSalaireDatas ;
    private List<SalaireData> salaireDatas;


    private List<RapportErreur> erreursEmploye;
    private List<RapportErreur> erreursGrille;
    private List<RapportErreur> erreursSalaire;

}
