package com.evaluation.erpnext_spring.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            filters.setLength(filters.length() - 1);  
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


   

    public boolean isSalarySlipAlreadyCreated(HttpSession session, String employeeId, String forDate) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextApiUrl + "/api/resource/Salary Slip?fields=[\"name\"]"
                + "&filters=[[\"employee\",\"=\",\"" + employeeId + "\"],[\"start_date\",\"<=\",\"" + forDate + "\"],[\"end_date\",\">=\",\"" + forDate + "\"]]";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<SalarySlipListResponse> response = restTemplate
                .exchange(url, HttpMethod.GET, entity, SalarySlipListResponse.class);
        
        return response.getStatusCode().is2xxSuccessful()
                && response.getBody() != null
                && !response.getBody().getData().isEmpty();
    }


    public boolean isSalarySlipAlreadyCreatedBack(HttpSession session, String employeeId, LocalDate forDate) {
       
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<SalarySlipDto> salarySlips = getSalarySlips(session, 0, 0, null).getData();

        for (SalarySlipDto slip : salarySlips) {
            if (!slip.getEmployee().equals(employeeId)) {
                continue; 
            }

            LocalDate startDate = LocalDate.parse(slip.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(slip.getEndDate(), formatter);

            
            if ((forDate.isEqual(startDate) || forDate.isAfter(startDate)) &&
                (forDate.isEqual(endDate) || forDate.isBefore(endDate))) {
                return true;
            }
        }

        return false;
    }



    public void cancelOrDeleteSalarySlip(HttpSession session, String slipName, boolean deleteIfPossible) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextApiUrl + "/api/resource/Salary Slip/" + slipName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        // headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        if (deleteIfPossible) {
            cancelOrDeleteSalarySlip(session, slipName, false);
            HttpEntity<String> deleteRequest = new HttpEntity<>(headers);
            ResponseEntity<String> deleteResponse = restTemplate.exchange(url, HttpMethod.DELETE, deleteRequest, String.class);

            if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de la suppression de la fiche de paie : " + slipName);
            }

        } else {
            
            Map<String, Object> cancelPayload = new HashMap<>();
            cancelPayload.put("docstatus", 2);

            HttpEntity<Map<String, Object>> cancelRequest = new HttpEntity<>(cancelPayload, headers);
            ResponseEntity<String> cancelResponse = restTemplate.exchange(url, HttpMethod.PUT, cancelRequest, String.class);

            if (!cancelResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de l'annulation de la fiche de paie : " + slipName);
            }
        }
    }




    
    


}
