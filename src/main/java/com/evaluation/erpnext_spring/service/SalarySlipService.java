package com.evaluation.erpnext_spring.service;

import java.util.Collections;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.salaries.SalarySlipListResponse;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDetail;
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
        "[\n"
            + "\"name\",\n"
            + "\"employee\",\n"
            + "\"employee_name\",\n"
            + "\"company\",\n"
            + "\"department\",\n"
            + "\"designation\",\n"
            + "\"start_date\",\n"
            + "\"end_date\",\n"
            + "\"posting_date\",\n"
            + "\"status\",\n"
            + "\"currency\",\n"
            + "\"payroll_frequency\",\n"
            + "\"salary_structure\",\n"
            + "\"mode_of_payment\",\n"
            + "\"total_working_days\",\n"
            + "\"payment_days\",\n"
            + "\"gross_pay\",\n"
            + "\"base_gross_pay\",\n"
            + "\"total_deduction\",\n"
            + "\"base_total_deduction\",\n"
            + "\"net_pay\",\n"
            + "\"base_net_pay\",\n"
            + "\"rounded_total\",\n"
            + "\"total_in_words\",\n"
            + "\"ctc\",\n"
            + "\"total_income_tax\",\n"
            + "\"earnings\"\n"
            + "]";
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

        System.out.println(urlBuilder.toString());
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
}
