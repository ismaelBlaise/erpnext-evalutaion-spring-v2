package com.evaluation.erpnext_spring.service.imports;

import com.evaluation.erpnext_spring.dto.imports.EmployeData;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeImportService {

    @Autowired
    private RapportErreurService rapportErreurService;

    public ResultatImport importEmployesFromCSV(ResultatImport resultatImport, MultipartFile file) throws IOException {
        if (!file.getContentType().equals("text/csv")) {
            throw new IllegalArgumentException("Seuls les fichiers CSV sont acceptés");
        }

        HeaderColumnNameMappingStrategy<EmployeData> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(EmployeData.class);

        List<EmployeData> validEmployes = new ArrayList<>();
        List<RapportErreur> erreurs = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<EmployeData> csvToBean = new CsvToBeanBuilder<EmployeData>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<EmployeData> employes = csvToBean.parse();

            int ligne = 1; // ligne 1 = en-tête
            for (EmployeData employe : employes) {
                List<RapportErreur> raison = validateEmploye(employe, ligne);
                if (raison.isEmpty()) {
                    validEmployes.add(employe);
                    employe.setDateEmbauche(DateUtils.normalizeToStandardFormat(employe.getDateEmbauche()).toString());
                    employe.setDateNaissance(DateUtils.normalizeToStandardFormat(employe.getDateNaissance()).toString());
                } else {
                    erreurs.addAll(raison);
                }
                ligne++;
            }
        }

        resultatImport.setEmployesValides(validEmployes);
        resultatImport.setErreursEmploye(erreurs);
        return resultatImport;
    }

    private List<RapportErreur> validateEmploye(EmployeData employe, int ligne) {
        List<RapportErreur> rapportErreurs = new ArrayList<>();
        
        if (employe.getRef() == null || employe.getRef().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Ref manquant", employe.getRef()));
        }
        if (employe.getNom() == null || employe.getNom().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Nom manquant", employe.getNom()));
        }
        if (employe.getPrenom() == null || employe.getPrenom().isEmpty()) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Prenom manquant", employe.getPrenom()));
        }
        if (employe.getDateNaissance() == null || !DateUtils.isValidDate(employe.getDateNaissance())) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Date d'anniversaire invalide", employe.getDateNaissance()));
        }
        if (employe.getDateEmbauche() == null || !DateUtils.isValidDate(employe.getDateEmbauche())) {
            rapportErreurs.add(rapportErreurService.createError(ligne, "Date d'embauche invalide", employe.getDateEmbauche()));
        }
        
        return rapportErreurs; 
    }
}