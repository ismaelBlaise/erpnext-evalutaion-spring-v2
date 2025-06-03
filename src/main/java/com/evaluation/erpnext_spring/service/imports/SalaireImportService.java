package com.evaluation.erpnext_spring.service.imports;

import com.evaluation.erpnext_spring.dto.imports.SalaireData;
import com.evaluation.erpnext_spring.dto.imports.RapportErreur;
import com.evaluation.erpnext_spring.dto.imports.ResultatImport;
import com.evaluation.erpnext_spring.utils.DateUtils;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalaireImportService {

    @Autowired
    private RapportErreurService rapportErreurService;

    public ResultatImport importSalairesFromCSV(ResultatImport resultatImport, MultipartFile file) throws IOException {
        if (!file.getContentType().equals("text/csv")) {
            throw new IllegalArgumentException("Seuls les fichiers CSV sont acceptés");
        }

        HeaderColumnNameMappingStrategy<SalaireData> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(SalaireData.class);

        List<SalaireData> validSalaires = new ArrayList<>();
        List<RapportErreur> erreurs = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<SalaireData> csvToBean = new CsvToBeanBuilder<SalaireData>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<SalaireData> salaires = csvToBean.parse();

            int ligne = 1; 
            for (SalaireData salaire : salaires) {
                List<RapportErreur> raison = validateSalaire(salaire, ligne);
                if (raison.isEmpty()) {
                    validSalaires.add(salaire);
                     
                    if (salaire.getMois() != null) {
                        salaire.setMois(DateUtils.normalizeToStandardFormat(salaire.getMois()).toString());
                    }
                } else {
                    erreurs.addAll(raison);
                }
                ligne++;
            }
        }

        resultatImport.setSalaireDatas(validSalaires);
        resultatImport.setErreursSalaire(erreurs);
        return resultatImport;
    }

    private List<RapportErreur> validateSalaire(SalaireData salaire, int ligne) {
        List<RapportErreur> rapportErreurs = new ArrayList<>();
        
        try {
            if (salaire.getMois() == null) {
                rapportErreurs.add(rapportErreurService.createError(ligne, "Mois manquant ou invalide", null));
            }
        } catch (DateTimeParseException e) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Format de mois invalide", salaire.getMois() != null ? salaire.getMois().toString() : null));
        }
        
        if (salaire.getRefEmploye() == null || salaire.getRefEmploye().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Référence employé manquante", salaire.getRefEmploye()));
        }
        
        if (salaire.getSalaireBase() == null || salaire.getSalaireBase() <= 0) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Salaire base invalide", 
                salaire.getSalaireBase() != null ? salaire.getSalaireBase().toString() : null));
        }
        
        if (salaire.getSalaryStructure() == null || salaire.getSalaryStructure().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Structure salariale manquante", salaire.getSalaryStructure()));
        }
        
        return rapportErreurs;
    }
}