package com.evaluation.erpnext_spring.service.imports;

import org.springframework.stereotype.Service;

import com.evaluation.erpnext_spring.dto.imports.RapportErreur;

@Service
public class RapportErreurService {
    public RapportErreur createError(int ligne, String raison, String valeur) {
        RapportErreur rapportErreur = new RapportErreur();
        rapportErreur.setFichier("file 2");
        rapportErreur.setLigne(ligne + "");
        rapportErreur.setRaison(raison);
        rapportErreur.setValeur(valeur);
        return rapportErreur;
    }
}
