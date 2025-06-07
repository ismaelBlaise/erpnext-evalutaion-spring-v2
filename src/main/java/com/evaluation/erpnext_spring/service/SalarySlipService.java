package com.evaluation.erpnext_spring.service;

import java.util.ArrayList;
import java.util.Collections;
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

        
        boolean hasDateFilter = filter != null 
            && ( (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) 
              || (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) );

        if (!hasDateFilter) {
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

                // Recherche dans les earnings
                if(salarySlipDto.getEarnings()!=null){
                    for (SalaryEarning salaryEarning : salarySlipDto.getEarnings()) {
                    if (salaryEarning.getSalaryComponent().equals(dataDto.getName())) {
                        componentsData.add(salaryEarning.getAmount());
                        found = true;
                        break;
                    }
                }
                }

                // Recherche dans les deductions si non trouvé dans earnings
                if (!found && salarySlipDto.getDeductions()!=null) {
                    for (SalaryDeduction salaryDeduction : salarySlipDto.getDeductions()) {
                        if (salaryDeduction.getSalaryComponent().equals(dataDto.getName())) {
                            componentsData.add(salaryDeduction.getAmount());
                            found = true;
                            break;
                        }
                    }
                }

                // Si toujours pas trouvé, ajouter 0
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
        if(year!=null){
            filter.setStartDate(year + "-01-01");
            filter.setEndDate(year + "-12-31");
        }

        List<SalarySlipDto> salarySlipDtos=getSalarySlips(session, 0, 0, filter).getData();
        Map<String, List<SalarySlipDto>> grouped = new TreeMap<>();

        for (SalarySlipDto slip : salarySlipDtos) {
            String month = slip.getPostingDate().substring(0, 7);
            grouped.computeIfAbsent(month, k -> new ArrayList<>()).add(slip);
        }

        Map<String, SalarySlipDto> consolidated = new TreeMap<>();
        for (Map.Entry<String, List<SalarySlipDto>> entry : grouped.entrySet()) {
            String month = entry.getKey();
            List<SalarySlipDto> slips = entry.getValue();
            SalarySlipListResponse salarySlipListResponse=new SalarySlipListResponse();
            salarySlipListResponse.setData(slips);

            List<SalarySlipDto> enriched = getRapport(session,salarySlipListResponse).getData();
                
            enriched = getComponents(enriched, dataDtos);

                
            SalarySlipDto monthlySlip = new SalarySlipDto();
            monthlySlip.setPostingDate(month);
            monthlySlip.setMois(DateUtils.getMonthName(month));

            monthlySlip.setCurrency(slips.get(0).getCurrency());

            double totalGross = 0;
            double totalDeduction = 0;
            double totalNet = 0;
            List<Double> totalComponents = new ArrayList<>();
            for (int i = 0; i < dataDtos.size(); i++) totalComponents.add(0.0);

            SalaryTotalsResponse salaryTotalsResponse=new SalaryTotalsResponse(enriched, dataDtos);

            totalGross+=salaryTotalsResponse.getTotalGrossPay();
            totalDeduction+=salaryTotalsResponse.getTotalDeductions();
            totalNet+=salaryTotalsResponse.getTotalNetPay();
            totalComponents=salaryTotalsResponse.getComponentsSum();

            monthlySlip.setGrossPay(totalGross);
            monthlySlip.setTotalDeduction(totalDeduction);
            monthlySlip.setNetPay(totalNet);
            monthlySlip.setComponentsDef(totalComponents);

            consolidated.put(month, monthlySlip);
        }

        return consolidated;
    }

}
