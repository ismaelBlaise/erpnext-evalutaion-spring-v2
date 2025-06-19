package com.evaluation.erpnext_spring.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.imports.SalaireData;
import com.evaluation.erpnext_spring.dto.salaries.SalaryDeduction;
import com.evaluation.erpnext_spring.dto.salaries.SalaryEarning;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDetail;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.evaluation.erpnext_spring.dto.structures.StructureAssignement;
import com.evaluation.erpnext_spring.service.imports.SalaireImportService;

import jakarta.servlet.http.HttpSession;

@Service
public class PaiementService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;
    
    @Autowired
    private StructureService structureService;

    @Autowired
    private SalaireImportService salaireImportService;


    @Autowired
    private SalarySlipService salarySlipService;

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





    // public List<SalarySlipDto> cancelAndUpdateSalarySlips(HttpSession session,
    //                                  List<SalarySlipDto> salarySlipNames,
    //                                  String componentName,
    //                                  double percentageChange,
    //                                  boolean isIncrease) throws Exception {
    //     String sid = (String) session.getAttribute("sid");
    //     if (sid == null || sid.isEmpty()) {
    //         throw new RuntimeException("Session non authentifiée");
    //     }

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    //     headers.add("Cookie", "sid=" + sid);

    //     List<SalarySlipDto> updatedSlips = new ArrayList<>();

    //     for (SalarySlipDto slipDto : salarySlipNames) {
    //         try {
    //             // 1. Récupérer le détail du Salary Slip original
    //             SalarySlipDetail originalDetail = getSalarySlipByName(session, slipDto.getName());
    //             SalarySlipDto originalSlip = originalDetail.getData();

    //             // 2. Annuler si docstatus == 1 (soumis)
    //             if (originalSlip.getDocStatus() == 1) {
    //                 String cancelUrl = erpnextApiUrl + "/api/resource/Salary Slip/" + originalSlip.getName();
    //                 Map<String, Object> cancelPayload = new HashMap<>();
    //                 cancelPayload.put("docstatus", 2); // Annulé

    //                 HttpEntity<Map<String, Object>> cancelRequest = new HttpEntity<>(cancelPayload, headers);
    //                 ResponseEntity<String> cancelResponse = restTemplate.exchange(cancelUrl, HttpMethod.PUT, cancelRequest, String.class);

    //                 if (cancelResponse.getStatusCode() != HttpStatus.OK) {
    //                     throw new RuntimeException("Échec de l'annulation de la fiche de paie : " + originalSlip.getName());
    //                 }
    //             }

    //             // 3. Cloner earnings avec montant modifié
    //             List<SalaryEarning> modifiedEarnings = new ArrayList<>();
    //             if (originalSlip.getEarnings() != null) {
    //                 for (SalaryEarning earning : originalSlip.getEarnings()) {
    //                     double amount = earning.getAmount();
    //                     // if (earning.getSalaryComponent().equals(componentName)) {
    //                     if (earning.getSalaryComponent().equals("Salaire base")) {
                            
    //                         amount = isIncrease
    //                                 ? amount * (1 + percentageChange / 100.0)
    //                                 : amount * (1 - percentageChange / 100.0);
    //                         amount = Math.round(amount * 100.0) / 100.0;
    //                         System.out.println(amount);
    //                     }
    //                     earning.setAmount(amount);
    //                     modifiedEarnings.add(earning);
    //                 }
    //             }

    //             // 4. Cloner deductions avec montant modifié
    //             List<SalaryDeduction> modifiedDeductions = new ArrayList<>();
    //             if (originalSlip.getDeductions() != null) {
    //                 for (SalaryDeduction deduction : originalSlip.getDeductions()) {
    //                     double amount = deduction.getAmount();
    //                     if (deduction.getSalaryComponent().equals(componentName)) {
    //                         amount = isIncrease
    //                                 ? amount * (1 + percentageChange / 100.0)
    //                                 : amount * (1 - percentageChange / 100.0);
    //                         amount = Math.round(amount * 100.0) / 100.0;
    //                     }
    //                     deduction.setAmount(amount);
    //                     modifiedDeductions.add(deduction);
    //                 }
    //             }

    //             // 5. Créer la nouvelle fiche avec les montants déjà modifiés + docstatus = 1 (soumis directement)
    //             Map<String, Object> newSlipPayload = new HashMap<>();
    //             newSlipPayload.put("employee", originalSlip.getEmployee());
    //             newSlipPayload.put("start_date", originalSlip.getStartDate());
    //             newSlipPayload.put("end_date", originalSlip.getEndDate());
    //             newSlipPayload.put("company", originalSlip.getCompany());
    //             newSlipPayload.put("posting_date", originalSlip.getPostingDate());
    //             newSlipPayload.put("salary_structure", originalSlip.getSalaryStructure());
    //             newSlipPayload.put("earnings", modifiedEarnings);
    //             newSlipPayload.put("deductions", modifiedDeductions);
    //             newSlipPayload.put("docstatus", 1); // Soumettre immédiatement
    //             newSlipPayload.put("parent", originalSlip.getName());
    //             // 6. Appel API de création
    //             String createUrl = erpnextApiUrl + "/api/resource/Salary Slip";
    //             HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(newSlipPayload, headers);
    //             ResponseEntity<SalarySlipDetail> createResponse = restTemplate.exchange(
    //                 createUrl,
    //                 HttpMethod.POST,
    //                 createRequest,
    //                 SalarySlipDetail.class
    //             );

    //             if (createResponse.getStatusCode() != HttpStatus.OK) {
    //                 throw new RuntimeException("Échec de la création/validation du nouveau Salary Slip.");
    //             }

    //             SalarySlipDto newSlip = createResponse.getBody().getData();
    //             updatedSlips.add(newSlip);

    //         } catch (Exception e) {
    //             throw new Exception("Erreur lors du traitement de la Salary Slip " + slipDto.getName() + " : " + e.getMessage(), e);
    //         }
    //     }

    //     return updatedSlips;
    // }






    public List<SalarySlipDto> cancelAndUpdateSalarySlips(HttpSession session,
                                                           List<SalarySlipDto> salarySlipDtos,
                                                           String componentName,
                                                           double percentageChange,
                                                           boolean isIncrease) throws Exception {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        List<SalarySlipDto> updatedSlips = new ArrayList<>();
        HttpHeaders headers = buildHeadersWithSid(sid);

        for (SalarySlipDto slipDto : salarySlipDtos) {
            try {
                SalarySlipDto updatedSlip = cancelAndUpdateSalarySlip(session, headers, slipDto.getName(), componentName, percentageChange, isIncrease);
                updatedSlips.add(updatedSlip);
            } catch (Exception e) {
                throw new Exception("Erreur lors du traitement de la Salary Slip " + slipDto.getName() + " : " + e.getMessage(), e);
            }
        }

        return updatedSlips;
    }

    private SalarySlipDto cancelAndUpdateSalarySlip(HttpSession session,
                                                    HttpHeaders headers,
                                                    String slipName,
                                                    String componentName,
                                                    double percentageChange,
                                                    boolean isIncrease) {

        SalarySlipDetail originalDetail = salarySlipService.getSalarySlipByName(session, slipName);
        SalarySlipDto originalSlip = originalDetail.getData();

        if (originalSlip.getDocStatus() == 1) {
            cancelSalarySlip(headers, originalSlip.getName());
        }

        List<SalaryEarning> modifiedEarnings = modifyEarnings(originalSlip.getEarnings(), componentName, percentageChange, isIncrease);
        List<SalaryDeduction> modifiedDeductions = modifyDeductions(originalSlip.getDeductions(), componentName, percentageChange, isIncrease);

        return createNewSalarySlip(headers, originalSlip, modifiedEarnings, modifiedDeductions);
    }

    private HttpHeaders buildHeadersWithSid(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        return headers;
    }

    private void cancelSalarySlip(HttpHeaders headers, String slipName) {
        String cancelUrl = erpnextApiUrl + "/api/resource/Salary Slip/" + slipName;
        Map<String, Object> cancelPayload = new HashMap<>();
        cancelPayload.put("docstatus", 2);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(cancelPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(cancelUrl, HttpMethod.PUT, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de l'annulation de la fiche de paie : " + slipName);
        }
    }

    private List<SalaryEarning> modifyEarnings(List<SalaryEarning> earnings,
                                               String componentName,
                                               double percentageChange,
                                               boolean isIncrease) {
        List<SalaryEarning> modified = new ArrayList<>();
        if (earnings != null) {
            for (SalaryEarning earning : earnings) {
                double amount = earning.getAmount();
                if (earning.getSalaryComponent().equals(componentName)) {
                    amount = isIncrease
                            ? amount * (1 + percentageChange / 100.0)
                            : amount * (1 - percentageChange / 100.0);
                    amount = Math.round(amount * 100.0) / 100.0;
                }
                earning.setAmount(amount);
                modified.add(earning);
            }
        }
        return modified;
    }

    private List<SalaryDeduction> modifyDeductions(List<SalaryDeduction> deductions,
                                                   String componentName,
                                                   double percentageChange,
                                                   boolean isIncrease) {
        List<SalaryDeduction> modified = new ArrayList<>();
        if (deductions != null) {
            for (SalaryDeduction deduction : deductions) {
                double amount = deduction.getAmount();
                if (deduction.getSalaryComponent().equals(componentName)) {
                    amount = isIncrease
                            ? amount * (1 + percentageChange / 100.0)
                            : amount * (1 - percentageChange / 100.0);
                    amount = Math.round(amount * 100.0) / 100.0;
                }
                deduction.setAmount(amount);
                modified.add(deduction);
            }
        }
        return modified;
    }

    private SalarySlipDto createNewSalarySlip(HttpHeaders headers,
                                              SalarySlipDto originalSlip,
                                              List<SalaryEarning> earnings,
                                              List<SalaryDeduction> deductions) {
        String createUrl = erpnextApiUrl + "/api/resource/Salary Slip";

        Map<String, Object> payload = new HashMap<>();
        payload.put("employee", originalSlip.getEmployee());
        payload.put("start_date", originalSlip.getStartDate());
        payload.put("end_date", originalSlip.getEndDate());
        payload.put("company", originalSlip.getCompany());
        payload.put("posting_date", originalSlip.getPostingDate());
        payload.put("salary_structure", originalSlip.getSalaryStructure());
        payload.put("earnings", earnings);
        payload.put("deductions", deductions);
        payload.put("docstatus", 1);
        payload.put("parent", originalSlip.getName());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<SalarySlipDetail> response = restTemplate.exchange(
                createUrl,
                HttpMethod.POST,
                request,
                SalarySlipDetail.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de la création du nouveau Salary Slip.");
        }

        return response.getBody().getData();
    }
}
