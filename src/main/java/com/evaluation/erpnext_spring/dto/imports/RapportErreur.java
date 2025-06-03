package com.evaluation.erpnext_spring.dto.imports;

import lombok.Data;

@Data
public class RapportErreur {
    private String ligne;
    private String raison;
    private String valeur;
}
