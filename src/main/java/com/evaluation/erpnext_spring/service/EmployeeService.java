package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.employees.EmployeeFilter;
import com.evaluation.erpnext_spring.dto.employees.EmployeeListResponse;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;

@Service
public class EmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    public EmployeeListResponse getAllEmployees(HttpSession session, int start, int pageLength,EmployeeFilter filter) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }
        String fields = "[\"name\",\"employee_name\",\"first_name\",\"last_name\",\"status\",\"company\",\"department\",\"designation\",\"gender\",\"date_of_joining\",\"employment_type\",\"branch\",\"company_email\"]";

        String filtre=buildFilters(filter);
        StringBuilder urlBuilder = new StringBuilder(erpnextApiUrl + "/api/resource/Employee?");
        

        if(filtre.isBlank()){
            urlBuilder.append("limit_start=").append(start)
                .append("&limit_page_length=").append(pageLength);
        }
        
        urlBuilder.append("&fields=").append(fields);
        urlBuilder.append(filtre);

       
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
    
    
        HttpEntity<String> request = new HttpEntity<>(headers);
    
        try {
            ResponseEntity<EmployeeListResponse> response = restTemplate.exchange(
                urlBuilder.toString(),
                HttpMethod.GET,
                request,
                EmployeeListResponse.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch employees: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while fetching employees: " + e.getMessage(), e);
        }
    }


    private String buildFilters(EmployeeFilter filter) {
        StringBuilder filters = new StringBuilder("&filters=[");
        if(filter==null){
            return "";
        }
        boolean hasFilter = false;

        if (filter.getEmployeeName() != null && !filter.getEmployeeName().isEmpty()) {
            filters.append("[\"employee_name\", \"like\", \"%").append(filter.getEmployeeName()).append("%\"],");
            hasFilter = true;
        }
        if (filter.getPosition() != null && !filter.getPosition().isEmpty()) {
            filters.append("[\"designation\", \"=\", \"").append(filter.getPosition()).append("\"],");
            hasFilter = true;
        }
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            filters.append("[\"department\", \"=\", \"").append(filter.getDepartment()).append("\"],");
            hasFilter = true;
        }
        if (filter.getCompany() != null && !filter.getCompany().isEmpty()) {
            filters.append("[\"company\", \"=\", \"").append(filter.getCompany()).append("\"],");
            hasFilter = true;
        }
        if (filter.getHireDateStart() != null && !filter.getHireDateStart().isEmpty()) {
            filters.append("[\"date_of_joining\", \">=\", \"").append(filter.getHireDateStart()).append("\"],");
            hasFilter = true;
        }
        if (filter.getHireDateEnd() != null && !filter.getHireDateEnd().isEmpty()) {
            filters.append("[\"date_of_joining\", \"<=\", \"").append(filter.getHireDateEnd()).append("\"],");
            hasFilter = true;
        }

        if (hasFilter) {
           
            filters.setLength(filters.length() - 1);
            filters.append("]");
            return filters.toString();
        }

        return ""; // aucun filtre
    }

    
    
}