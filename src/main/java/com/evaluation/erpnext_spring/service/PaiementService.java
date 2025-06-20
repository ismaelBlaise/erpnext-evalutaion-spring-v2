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
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipListResponse;
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
        // System.out.println(base);
        SalaireData salaireData = new SalaireData();
        salaireData.setMois(date);
        salaireData.setRefEmploye(employeeId);
        if(base==0.0){
            salaireData.setSalaireBase(lastAssignement.getBase());
        }
        else{
            salaireData.setSalaireBase(base);
        }
        salaireData.setSalaryStructure(lastAssignement.getSalary_structure());

        return salaireData;
    }

    public void genererSalaires(HttpSession session, String employee, String startDate, String endDate, Double base) throws Exception {
        List<SalaireData> salaireDatas = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter).withDayOfMonth(1);
        LocalDate end = LocalDate.parse(endDate, formatter).withDayOfMonth(1);
        

        while (!start.isAfter(end)) {
            if(salarySlipService.isSalarySlipAlreadyCreatedBack(session, employee, start)==false){
                String dateMois = start.toString(); 
                SalaireData salaireData = genererSalaireData(session, employee, dateMois, base);
                System.out.println(salaireData.getSalaireBase());
                salaireDatas.add(salaireData);
                start = start.plusMonths(1);
            }
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



     public List<SalarySlipDto> getSalarySlipsByComponentThreshold(HttpSession session, 
                                                             String componentName, 
                                                             double threshold, 
                                                             boolean isGreaterThan) {
        
        SalarySlipListResponse response = salarySlipService.getSalarySlips(session, 0, 0, null);
        List<SalarySlipDto> allSlips = response.getData();
        
        
        List<SalarySlipDto> filteredSlips = new ArrayList<>();
        
        
        for (SalarySlipDto slip : allSlips) {
            SalarySlipDetail detail = salarySlipService.getSalarySlipByName(session, slip.getName());
            SalarySlipDto fullSlip = detail.getData();
            
            
            if (fullSlip.getEarnings() != null) {
                for (SalaryEarning earning : fullSlip.getEarnings()) {
                    if (earning.getSalaryComponent().equals(componentName)) {
                        
                        if ((isGreaterThan && earning.getAmount() > threshold) ||
                            (!isGreaterThan && earning.getAmount() < threshold)) {
                           
                                filteredSlips.add(fullSlip);
                            break;  
                        }
                    }
                }
            }
            
            
            if (fullSlip.getDeductions() != null && !filteredSlips.contains(fullSlip)) {
                for (SalaryDeduction deduction : fullSlip.getDeductions()) {
                    if (deduction.getSalaryComponent().equals(componentName)) {
                        if ((isGreaterThan && deduction.getAmount() > threshold) ||
                            (!isGreaterThan && deduction.getAmount() < threshold)) {
                           
                            filteredSlips.add(fullSlip);
                            break;
                        }
                    }
                }
            }
        }
        
        return filteredSlips;
    }



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
                StructureAssignement structureAssignement=structureService.getLastStructureAssignementBeforeDate(session, slipDto.getEmployee(), slipDto.getStartDate());
                structureService.cancelOrDeleteStructureAssignment(session, structureAssignement.getName(), true);
                double amount = structureAssignement.getBase();
                amount = isIncrease
                            ? amount * (1 + percentageChange / 100.0)
                            : amount * (1 - percentageChange / 100.0);
                amount = Math.round(amount * 100.0) / 100.0;
                structureAssignement.setBase(amount);
                List<SalaireData> salaireDatas=new ArrayList<>();
                SalaireData salaireData=new SalaireData();
                salaireData.setMois(structureAssignement.getFrom_date());
                salaireData.setRefEmploye(structureAssignement.getEmployee());
                salaireData.setSalaireBase(structureAssignement.getBase());
                salaireData.setSalaryStructure(structureAssignement.getSalary_structure());

                salaireDatas.add(salaireData);
                salaireImportService.importSalaireData(session, salaireDatas);
                cancelSalarySlip(headers, slipDto.getName());
                // SalarySlipDto updatedSlip = cancelAndUpdateSalarySlip(session, headers, slipDto.getName(), componentName, percentageChange, isIncrease);
                updatedSlips.add(slipDto);
            } catch (Exception e) {
                throw new Exception("Erreur lors du traitement de la Salary Slip " + slipDto.getName() + " : " + e.getMessage(), e);
            }
        }

        return updatedSlips;
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

    
}
