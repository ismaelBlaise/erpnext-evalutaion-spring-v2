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



    // public Map<String, SalarySlipDto> getSalarySlipsGroupedByMonth(HttpSession session, String year, List<DataDto> dataDtos) {
    //     String sid = (String) session.getAttribute("sid");
    //     if (sid == null || sid.isEmpty()) {
    //         throw new RuntimeException("Session non authentifiée");
    //     }

    //     SalarySlipFilter filter = new SalarySlipFilter();
        
    //     if(year!=null){
    //         filter.setStartDate(year + "-01-01");
    //         filter.setEndDate(year + "-12-31");
    //     }


    //     List<SalarySlipDto> salarySlipDtos=getSalarySlips(session, 0, 0, filter).getData();
    //     Map<String, List<SalarySlipDto>> grouped = new TreeMap<>();

    //     for (SalarySlipDto slip : salarySlipDtos) {
    //         String month = slip.getPostingDate().substring(0, 7);
    //         grouped.computeIfAbsent(month, k -> new ArrayList<>()).add(slip);
    //     }

    //     Map<String, SalarySlipDto> consolidated = new TreeMap<>();
    //     for (Map.Entry<String, List<SalarySlipDto>> entry : grouped.entrySet()) {
    //         String month = entry.getKey();
    //         List<SalarySlipDto> slips = entry.getValue();
    //         SalarySlipListResponse salarySlipListResponse=new SalarySlipListResponse();
    //         salarySlipListResponse.setData(slips);

    //         List<SalarySlipDto> enriched = getRapport(session,salarySlipListResponse).getData();
                
    //         enriched = getComponents(enriched, dataDtos);

                
    //         SalarySlipDto monthlySlip = new SalarySlipDto();
    //         monthlySlip.setPostingDate(month);
    //         monthlySlip.setMois(DateUtils.getMonthName(month));

    //         monthlySlip.setCurrency(slips.get(0).getCurrency());

    //         double totalGross = 0;
    //         double totalDeduction = 0;
    //         double totalNet = 0;
    //         List<Double> totalComponents = new ArrayList<>();
    //         for (int i = 0; i < dataDtos.size(); i++) totalComponents.add(0.0);

    //         SalaryTotalsResponse salaryTotalsResponse=new SalaryTotalsResponse(enriched, dataDtos);

    //         totalGross+=salaryTotalsResponse.getTotalGrossPay();
    //         totalDeduction+=salaryTotalsResponse.getTotalDeductions();
    //         totalNet+=salaryTotalsResponse.getTotalNetPay();
    //         totalComponents=salaryTotalsResponse.getComponentsSum();

    //         monthlySlip.setGrossPay(totalGross);
    //         monthlySlip.setTotalDeduction(totalDeduction);
    //         monthlySlip.setNetPay(totalNet);
    //         monthlySlip.setComponentsDef(totalComponents);

    //         consolidated.put(month, monthlySlip);
    //     }

    //     return consolidated;
    // }


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



    public List<SalarySlipDto> cancelAndUpdateSalarySlips(HttpSession session,
                                     List<SalarySlipDto> salarySlipNames,
                                     String componentName,
                                     double percentageChange,
                                     boolean isIncrease) throws Exception {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);

        List<SalarySlipDto> updatedSlips = new ArrayList<>();

        for (SalarySlipDto slipDto : salarySlipNames) {
            try {
                // 1. Récupérer le détail du Salary Slip original
                SalarySlipDetail originalDetail = getSalarySlipByName(session, slipDto.getName());
                SalarySlipDto originalSlip = originalDetail.getData();

                // 2. Annuler si docstatus == 1 (soumis)
                if (originalSlip.getDocStatus() == 1) {
                    String cancelUrl = erpnextApiUrl + "/api/resource/Salary Slip/" + originalSlip.getName();
                    Map<String, Object> cancelPayload = new HashMap<>();
                    cancelPayload.put("docstatus", 2); // Annulé

                    HttpEntity<Map<String, Object>> cancelRequest = new HttpEntity<>(cancelPayload, headers);
                    ResponseEntity<String> cancelResponse = restTemplate.exchange(cancelUrl, HttpMethod.PUT, cancelRequest, String.class);

                    if (cancelResponse.getStatusCode() != HttpStatus.OK) {
                        throw new RuntimeException("Échec de l'annulation de la fiche de paie : " + originalSlip.getName());
                    }
                }

                // 3. Construire un nouveau Salary Slip basé sur l'original
                Map<String, Object> newSlipPayload = new HashMap<>();
                newSlipPayload.put("employee", originalSlip.getEmployee());
                newSlipPayload.put("start_date", originalSlip.getStartDate());
                newSlipPayload.put("end_date", originalSlip.getEndDate());
                newSlipPayload.put("company", originalSlip.getCompany());
                newSlipPayload.put("posting_date", originalSlip.getPostingDate());
                newSlipPayload.put("salary_structure", originalSlip.getSalaryStructure());
                newSlipPayload.put("earnings", originalSlip.getEarnings());
                newSlipPayload.put("deductions", originalSlip.getDeductions());

                // 4. Créer le nouveau Salary Slip
                String createUrl = erpnextApiUrl + "/api/resource/Salary Slip";
                HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(newSlipPayload, headers);
                ResponseEntity<SalarySlipDetail> createResponse = restTemplate.exchange(
                    createUrl,
                    HttpMethod.POST,
                    createRequest,
                    SalarySlipDetail.class
                );

                if (createResponse.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Échec de la création du nouveau Salary Slip.");
                }

                SalarySlipDto newSlip = createResponse.getBody().getData();

                // 5. Modifier earnings et deductions
                if (newSlip.getEarnings() != null) {
                    for (SalaryEarning earning : newSlip.getEarnings()) {
                        if (earning.getSalaryComponent().equals(componentName)) {
                            double newAmount = isIncrease
                                    ? earning.getAmount() * (1 + percentageChange / 100.0)
                                    : earning.getAmount() * (1 - percentageChange / 100.0);
                            earning.setAmount(Math.round(newAmount * 100.0) / 100.0);
                        }
                    }
                }

                if (newSlip.getDeductions() != null) {
                    for (SalaryDeduction deduction : newSlip.getDeductions()) {
                        if (deduction.getSalaryComponent().equals(componentName)) {
                            double newAmount = isIncrease
                                    ? deduction.getAmount() * (1 + percentageChange / 100.0)
                                    : deduction.getAmount() * (1 - percentageChange / 100.0);
                            deduction.setAmount(Math.round(newAmount * 100.0) / 100.0);
                        }
                    }
                }

                // 6. Mettre à jour le nouveau Salary Slip avec les nouveaux montants
                String updateUrl = erpnextApiUrl + "/api/resource/Salary Slip/" + newSlip.getName();
                Map<String, Object> updatePayload = new HashMap<>();
                updatePayload.put("earnings", newSlip.getEarnings());
                updatePayload.put("deductions", newSlip.getDeductions());

                HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updatePayload, headers);
                ResponseEntity<String> updateResponse = restTemplate.exchange(
                    updateUrl,
                    HttpMethod.PUT,
                    updateRequest,
                    String.class
                );

                if (updateResponse.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Échec de la mise à jour du nouveau Salary Slip : " + newSlip.getName());
                }

                // 7. Soumettre (valider) le nouveau Salary Slip
                Map<String, Object> submitPayload = new HashMap<>();
                submitPayload.put("docstatus", 1); // Soumis

                HttpEntity<Map<String, Object>> submitRequest = new HttpEntity<>(submitPayload, headers);
                ResponseEntity<String> submitResponse = restTemplate.exchange(
                    updateUrl,
                    HttpMethod.PUT,
                    submitRequest,
                    String.class
                );

                if (submitResponse.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Échec de la validation du nouveau Salary Slip : " + newSlip.getName());
                }

                updatedSlips.add(newSlip);

            } catch (Exception e) {
                throw new Exception("Erreur lors du traitement de la Salary Slip " + slipDto.getName() + " : " + e.getMessage(), e);
            }
        }

        return updatedSlips;
    }



}
