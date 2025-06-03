package com.evaluation.erpnext_spring.service;

import com.evaluation.erpnext_spring.dto.imports.EmployeData;
import com.evaluation.erpnext_spring.dto.imports.RapportErreur;
import com.evaluation.erpnext_spring.dto.imports.ResultatImport;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    public ResultatImport importEmployesFromCSV(ResultatImport resultatImport,MultipartFile file) throws IOException {
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
                List<RapportErreur> raison = validateEmploye(employe,ligne);
                if (raison.isEmpty()) {
                    validEmployes.add(employe);

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

    private List<RapportErreur> validateEmploye(EmployeData employe,int ligne) {
        List<RapportErreur> rapportErreurs=new ArrayList<>();
        if (employe.getRef() == null || employe.getRef().isEmpty()) {
            RapportErreur rapportErreur=new RapportErreur();
            rapportErreur.setFichier("file 1");
            rapportErreur.setLigne(ligne+"");
            rapportErreur.setRaison("Ref manquant");
            rapportErreur.setValeur(employe.getRef());
            rapportErreurs.add(rapportErreur);
        }
        if (employe.getNom() == null || employe.getNom().isEmpty()) {
            RapportErreur rapportErreur=new RapportErreur();
            rapportErreur.setFichier("file 1");
            rapportErreur.setLigne(ligne+"");
            rapportErreur.setRaison("Nom manquant");
            rapportErreur.setValeur(employe.getNom());
            rapportErreurs.add(rapportErreur);
        }
        if (employe.getPrenom() == null || employe.getPrenom().isEmpty()) {
            RapportErreur rapportErreur=new RapportErreur();
            rapportErreur.setFichier("file 1");
            rapportErreur.setLigne(ligne+"");
            rapportErreur.setRaison("Prenom manquant");
            rapportErreur.setValeur(employe.getPrenom());
            rapportErreurs.add(rapportErreur);
        }
        if (employe.getDateNaissance() == null || !isValidDate(employe.getDateNaissance())) {
            RapportErreur rapportErreur=new RapportErreur();
            rapportErreur.setFichier("file 1");
            rapportErreur.setLigne(ligne+"");
            rapportErreur.setRaison("Date d'anniversaire invalide ou inexistante");
            rapportErreur.setValeur(employe.getDateEmbauche());
            rapportErreurs.add(rapportErreur);
        }
        if (employe.getDateEmbauche() == null || !isValidDate(employe.getDateEmbauche())) {
            RapportErreur rapportErreur=new RapportErreur();
            rapportErreur.setFichier("file 1");
            rapportErreur.setLigne(ligne+"");
            rapportErreur.setRaison("Date d'embauche invalide ou inexistante");
            rapportErreur.setValeur(employe.getDateEmbauche());
            rapportErreurs.add(rapportErreur);
        }
       
        return rapportErreurs; 
    }

    private boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
