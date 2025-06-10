package com.evaluation.erpnext_spring.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evaluation.erpnext_spring.dto.imports.SalaireData;
import com.evaluation.erpnext_spring.dto.structures.StructureAssignement;
import com.evaluation.erpnext_spring.service.imports.SalaireImportService;

import jakarta.servlet.http.HttpSession;

@Service
public class GenerationPaiementService {
    @Autowired
    private StructureService structureService;

    @Autowired
    private SalaireImportService salaireImportService;

    public SalaireData genererSalaireData(HttpSession session, String employeeId, String date, Double base) {
        StructureAssignement lastAssignement = structureService.getLastStructureAssignementBeforeDate(session, employeeId, date);

        if (lastAssignement == null) {
            throw new RuntimeException("Aucune structure de salaire trouvée pour l’employé " + employeeId + " avant la date " + date);
        }

        SalaireData salaireData = new SalaireData();
        salaireData.setMois(date);
        salaireData.setRefEmploye(employeeId);
        salaireData.setSalaireBase(base == 0 ? lastAssignement.getBase() : base);
        salaireData.setSalaryStructure(lastAssignement.getSalary_structure());

        return salaireData;
    }

    public void genererSalaires(HttpSession session, String employee, String startDate, String endDate, Double base) throws Exception {
        List<SalaireData> salaireDatas = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter).withDayOfMonth(1);
        LocalDate end = LocalDate.parse(endDate, formatter).withDayOfMonth(1);

        while (!start.isAfter(end)) {
            String dateMois = start.toString(); 
            SalaireData salaireData = genererSalaireData(session, employee, dateMois, base);
            salaireDatas.add(salaireData);
            start = start.plusMonths(1);
        }

        
        salaireImportService.importSalaireData(session, salaireDatas);
    }
}
