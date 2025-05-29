package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.evaluation.erpnext_spring.dto.company.CompanyListResponseDTO;
import com.evaluation.erpnext_spring.dto.company.CompanyDetailGroupDTO;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;

@Service
public class CompanyService {

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    @Autowired
    private RestTemplate restTemplate;

    public CompanyListResponseDTO getCompanies(HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String url = String.format("%s/api/resource/Company", erpnextApiUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<CompanyListResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CompanyListResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch companies: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("ERPNext API error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching companies: " + e.getMessage(), e);
        }
    }

    public CompanyDetailGroupDTO getCompanyByName(HttpSession session, String companyName) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String url = String.format("%s/api/resource/Company/%s", erpnextApiUrl, companyName);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<CompanyDetailGroupDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CompanyDetailGroupDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody(); 
            } else {
                throw new RuntimeException("Failed to fetch company: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("ERPNext API error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching company: " + e.getMessage(), e);
        }
    }
}
