package com.evaluation.erpnext_spring.service.imports;

import com.evaluation.erpnext_spring.dto.imports.GrilleSalaireData;
import com.evaluation.erpnext_spring.dto.imports.RapportErreur;
import com.evaluation.erpnext_spring.dto.imports.ResultatImport;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrilleImportService {

    @Autowired
    private RapportErreurService rapportErreurService;

    public ResultatImport importGrilleSalaireFromCSV(ResultatImport resultatImport, MultipartFile file) throws IOException {
        if (!file.getContentType().equals("text/csv")) {
            throw new IllegalArgumentException("Seuls les fichiers CSV sont acceptés");
        }

        HeaderColumnNameMappingStrategy<GrilleSalaireData> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(GrilleSalaireData.class);

        List<GrilleSalaireData> validGrilles = new ArrayList<>();
        List<RapportErreur> erreurs = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<GrilleSalaireData> csvToBean = new CsvToBeanBuilder<GrilleSalaireData>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<GrilleSalaireData> grilles = csvToBean.parse();

            int ligne = 1; // ligne 1 = en-tête
            for (GrilleSalaireData grille : grilles) {
                List<RapportErreur> raison = validateGrilleSalaire(grille, ligne);
                if (raison.isEmpty()) {
                    validGrilles.add(grille);
                } else {
                    erreurs.addAll(raison);
                }
                ligne++;
            }
        }

        resultatImport.setGrilleSalaireDatas(validGrilles);
        resultatImport.setErreursGrille(erreurs);
        return resultatImport;
    }

    private List<RapportErreur> validateGrilleSalaire(GrilleSalaireData grille, int ligne) {
        List<RapportErreur> rapportErreurs = new ArrayList<>();
        
        if (grille.getSalaryStructure() == null || grille.getSalaryStructure().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Salary structure manquant", grille.getSalaryStructure()));
        }
        
        if (grille.getName() == null || grille.getName().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Name manquant", grille.getName()));
        }
        
        if (grille.getAbbr() == null || grille.getAbbr().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Abbr manquant", grille.getAbbr()));
        }
        
        if (grille.getType() == null || grille.getType().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Type manquant", grille.getType()));
        }
        
        if (grille.getValeur() == null || grille.getValeur().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Valeur manquante", grille.getValeur()));
        }
        
        if (grille.getCompany() == null || grille.getCompany().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Company manquante", grille.getCompany()));
        }
        
        return rapportErreurs;
    }
    
    
}