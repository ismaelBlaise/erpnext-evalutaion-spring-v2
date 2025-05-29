package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.suppliers.SupplierListResponse;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;

@Service
public class SupplierService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    public SupplierListResponse getAllSuppliers(HttpSession session, int start, int pageLength) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }
    
        String url = erpnextApiUrl + "/api/resource/Supplier?limit_start=" + start + "&limit_page_length=" + pageLength;
    
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
    
        HttpEntity<String> request = new HttpEntity<>(headers);
    
        try {
            ResponseEntity<SupplierListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                SupplierListResponse.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch suppliers: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching suppliers: " + e.getMessage(), e);
        }
    }
    

    public SupplierListResponse getSupplierByName(HttpSession session, String name) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }
    
        String filters = String.format("[[\"Supplier\",\"supplier_name\",\"like\",\"%%%s%%\"]]", name);
        String url = erpnextApiUrl + "/api/resource/Supplier?filters=" + filters;
        System.out.println(url);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
    
        try {
            ResponseEntity<SupplierListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request, 
                SupplierListResponse.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch supplier by name: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching supplier by name: " + e.getMessage(), e);
        }
    }
    
}