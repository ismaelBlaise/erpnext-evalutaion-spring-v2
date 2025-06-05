package com.evaluation.erpnext_spring.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.data.DataListReponse;

import jakarta.servlet.http.HttpSession;

@Service
public class DataService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;


    public DataListReponse getAllData(HttpSession session,String type) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }
        String url=null;
        if(type.equals("Designation")){
            url = erpnextApiUrl + "/api/resource/Designation";
        }
        else if(type.equals("Department")){
            url = erpnextApiUrl + "/api/resource/Department";
        }
        else if(type.equals("Company")){
            url = erpnextApiUrl + "/api/resource/Company";
        }
        else if(type.equals("Salary Component")){
            url = erpnextApiUrl + "/api/resource/Salary Component";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        // headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
    
        HttpEntity<String> request = new HttpEntity<>(headers);
    
        try {
            ResponseEntity<DataListReponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                DataListReponse.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch designations: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching designations: " + e.getMessage(), e);
        }
    }

}
