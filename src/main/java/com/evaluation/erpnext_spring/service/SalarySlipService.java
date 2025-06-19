package com.evaluation.erpnext_spring.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.salaries.SalarySlipListResponse;
import com.evaluation.erpnext_spring.dto.salaries.SalaryTotalsResponse;
import com.evaluation.erpnext_spring.utils.DateUtils;
import com.evaluation.erpnext_spring.dto.data.DataDto;
import com.evaluation.erpnext_spring.dto.salaries.SalaryDeduction;
import com.evaluation.erpnext_spring.dto.salaries.SalaryEarning;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDetail;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipFilter;

@Service
public class SalarySlipService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    public String fields(){
        return
        "[\"*\"]";
    }
     
    public SalarySlipListResponse getSalarySlips(HttpSession session, int start, int pageLength, SalarySlipFilter filter) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String fields = fields();

        String filtersParam = buildFilters(filter);

        StringBuilder urlBuilder = new StringBuilder(erpnextApiUrl + "/api/resource/Salary Slip?");

        
        @SuppressWarnings("unused")
        boolean hasDateFilter = filter != null 
            && ( (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) 
              || (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) );

        // if (!hasDateFilter) {
        if(start!=0 && pageLength!=0){
            urlBuilder.append("limit_start=").append(start)
                      .append("&limit_page_length=").append(pageLength);
    
        }

        urlBuilder.append("&fields=").append(fields);

        if (!filtersParam.isEmpty()) {
            urlBuilder.append(filtersParam);
        }

       
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SalarySlipListResponse> response = restTemplate.exchange(
                urlBuilder.toString(),
                HttpMethod.GET,
                request,
                SalarySlipListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Échec de la récupération des fiches de paie : " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des fiches de paie : " + e.getMessage(), e);
        }
    }

       
    private String buildFilters(SalarySlipFilter filter) {
        if (filter == null) return "";

        StringBuilder filters = new StringBuilder("&filters=[");

        boolean hasFilter = false;

        if (filter.getEmployee() != null && !filter.getEmployee().isEmpty()) {
            filters.append("[\"employee\", \"=\", \"").append(filter.getEmployee()).append("\"],");
            hasFilter = true;
        }
        if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
            filters.append("[\"start_date\", \">=\", \"").append(filter.getStartDate()).append("\"],");
            hasFilter = true;
        }
        if (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) {
            filters.append("[\"end_date\", \"<=\", \"").append(filter.getEndDate()).append("\"],");
            hasFilter = true;
        }
        // if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
        //     filters.append("[\"posting_date\", \">=\", \"").append(filter.getStartDate()).append("\"],");
        //     hasFilter = true;
        // }
        // if (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) {
        //     filters.append("[\"posting_date\", \"<=\", \"").append(filter.getEndDate()).append("\"],");
        //     hasFilter = true;
        // }

        if (hasFilter) {
            filters.setLength(filters.length() - 1); // supprime la dernière virgule
            filters.append("]");
            return filters.toString();
        }

        return "";
    }


    public SalarySlipListResponse getRapport(HttpSession session,SalarySlipListResponse salarySlipListResponse){
        List<SalarySlipDto> salarySlipDtos=salarySlipListResponse.getData();
        SalarySlipListResponse salarySlipListResponse2=new SalarySlipListResponse();
        List<SalarySlipDto> salarySlipDtos2=new ArrayList<>();
        for (SalarySlipDto salarySlipDto : salarySlipDtos) {
            SalarySlipDto salarySlipDto2=getSalarySlipByName(session, salarySlipDto.getName()).getData();
            salarySlipDtos2.add(salarySlipDto2);
        }
        salarySlipListResponse2.setData(salarySlipDtos2);
        return salarySlipListResponse2;

    }

    public List<SalarySlipDto> getComponents(List<SalarySlipDto> salarySlipDtos, List<DataDto> dataDtos) {
        List<SalarySlipDto> salarySlipDtos2 = new ArrayList<>();

        for (SalarySlipDto salarySlipDto : salarySlipDtos) {
            List<Double> componentsData = new ArrayList<>();

            for (DataDto dataDto : dataDtos) {
                boolean found = false;

                
                if(salarySlipDto.getEarnings()!=null){
                    for (SalaryEarning salaryEarning : salarySlipDto.getEarnings()) {
                    if (salaryEarning.getSalaryComponent().equals(dataDto.getName())) {
                        componentsData.add(salaryEarning.getAmount());
                        found = true;
                        break;
                    }
                }
                }

                 
                if (!found && salarySlipDto.getDeductions()!=null) {
                    for (SalaryDeduction salaryDeduction : salarySlipDto.getDeductions()) {
                        if (salaryDeduction.getSalaryComponent().equals(dataDto.getName())) {
                            componentsData.add(salaryDeduction.getAmount());
                            found = true;
                            break;
                        }
                    }
                }

                
                if (!found) {
                    componentsData.add(0.0);
                }
            }

            salarySlipDto.setComponentsDef(componentsData);
            salarySlipDtos2.add(salarySlipDto);
        }

        return salarySlipDtos2;
    }

    


    public SalarySlipDetail getSalarySlipByName(HttpSession session, String name) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String url = erpnextApiUrl + "/api/resource/Salary Slip/" + name;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SalarySlipDetail> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                SalarySlipDetail.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Salary slip not found: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching salary slip: " + e.getMessage(), e);
        }
    }



    


    public Map<String, SalarySlipDto> getSalarySlipsGroupedByMonth(HttpSession session, String year, List<DataDto> dataDtos) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        SalarySlipFilter filter = new SalarySlipFilter();
        if (year != null) {
            filter.setStartDate(year + "-01-01");
            filter.setEndDate(year + "-12-31");
        }

        List<SalarySlipDto> salarySlipDtos = getSalarySlips(session, 0, 0, filter).getData();
        Map<String, List<SalarySlipDto>> grouped = new TreeMap<>();

        for (SalarySlipDto slip : salarySlipDtos) {
            String month = slip.getPostingDate().substring(0, 7); // ex: "2025-03"
            grouped.computeIfAbsent(month, k -> new ArrayList<>()).add(slip);
        }

        Map<String, SalarySlipDto> consolidated = new TreeMap<>();

        for (int monthNum = 1; monthNum <= 12; monthNum++) {
            String monthStr = String.format("%s-%02d", year, monthNum);
            List<SalarySlipDto> slips = grouped.get(monthStr);

            SalarySlipDto monthlySlip = new SalarySlipDto();
            monthlySlip.setPostingDate(monthStr);
            monthlySlip.setMois(DateUtils.getMonthName(monthStr));

            if (slips == null || slips.isEmpty()) {
                // Valeurs par défaut si aucun bulletin ce mois
                monthlySlip.setCurrency("EUR"); // ou autre valeur par défaut
                monthlySlip.setGrossPay(0.0);
                monthlySlip.setTotalDeduction(0.0);
                monthlySlip.setNetPay(0.0);

                // Créer une liste de composants tous à 0
                List<Double> componentsZeros = new ArrayList<>();
                for (int i = 0; i < dataDtos.size(); i++) {
                    componentsZeros.add(0.0);
                }
                monthlySlip.setComponentsDef(componentsZeros);
            } else {
                SalarySlipListResponse salarySlipListResponse = new SalarySlipListResponse();
                salarySlipListResponse.setData(slips);

                List<SalarySlipDto> enriched = getRapport(session, salarySlipListResponse).getData();
                enriched = getComponents(enriched, dataDtos);

                SalaryTotalsResponse salaryTotalsResponse = new SalaryTotalsResponse(enriched, dataDtos);

                monthlySlip.setCurrency(slips.get(0).getCurrency());
                monthlySlip.setGrossPay(salaryTotalsResponse.getTotalGrossPay());
                monthlySlip.setTotalDeduction(salaryTotalsResponse.getTotalDeductions());
                monthlySlip.setNetPay(salaryTotalsResponse.getTotalNetPay());
                monthlySlip.setComponentsDef(salaryTotalsResponse.getComponentsSum());
            }

            consolidated.put(monthStr, monthlySlip);
        }

        return consolidated;
    }


    public List<SalarySlipDto> getSalarySlipsByComponentThreshold(HttpSession session, 
                                                             String componentName, 
                                                             double threshold, 
                                                             boolean isGreaterThan) {
        
        SalarySlipListResponse response = getSalarySlips(session, 0, 0, null);
        List<SalarySlipDto> allSlips = response.getData();
        
        // System.out.println(allSlips.size());
        List<SalarySlipDto> filteredSlips = new ArrayList<>();
        
        
        for (SalarySlipDto slip : allSlips) {
            SalarySlipDetail detail = getSalarySlipByName(session, slip.getName());
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

        SalarySlipDetail originalDetail = getSalarySlipByName(session, slipName);
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
