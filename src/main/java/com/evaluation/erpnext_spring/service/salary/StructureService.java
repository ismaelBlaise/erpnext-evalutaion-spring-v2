package com.evaluation.erpnext_spring.service.salary;

import com.evaluation.erpnext_spring.dto.grilles.SalaryStructureDto;
import com.evaluation.erpnext_spring.dto.grilles.SalaryStructureResponse;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Service
public class StructureService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity<Map<String, Object>> createSalaryGrid(HttpSession session, SalaryStructureDto salaryGridDTO) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Session not authenticated"));
        }

        try {
             
            if(salaryGridDTO.getEarnings()!=null ){
                salaryGridDTO.getEarnings().forEach(c -> c.setType("Earning"));
            }
            if(salaryGridDTO.getDeductions()!=null){
                salaryGridDTO.getDeductions().forEach(c -> c.setType("Deduction"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Cookie", "sid=" + sid);

            HttpEntity<SalaryStructureDto> request = new HttpEntity<>(salaryGridDTO, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpnextApiUrl + "/api/resource/Salary Structure",
                    HttpMethod.POST,
                    request,
                    String.class);

            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = objectMapper.readValue(
                        response.getBody(), Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                String docName = (String) data.get("name");
                System.out.println(docName+"\n");

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(
                        Map.of("error", "Failed to create salary grid"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", e.getMessage()));
        }
    }

    
    public ResponseEntity<Map<String, Object>> submitSalaryGrid(HttpSession session,String docName) {
        try {
            String sid = (String) session.getAttribute("sid");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Cookie", "sid=" + sid);

             
            Map<String, Object> body = Map.of(
                "doc", Map.of(
                    "doctype", "Salary Structure",
                    "name", docName
                )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                erpnextApiUrl + "/api/method/frappe.client.submit",
                HttpMethod.POST,
                request,
                String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", "Échec de la validation de la grille salariale"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


   public SalaryStructureResponse getSalaryStructures(HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

       
        

        StringBuilder urlBuilder = new StringBuilder(erpnextApiUrl + "/api/resource/Salary Structure?");

        
       
       
        urlBuilder.append("&fields=").append("[\"*\"]");

       
       
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SalaryStructureResponse> response = restTemplate.exchange(
                urlBuilder.toString(),
                HttpMethod.GET,
                request,
                SalaryStructureResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Échec de la récupération des grilles de paie : " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des grilles de paie : " + e.getMessage(), e);
        }
    }


    // public double moyenneSalaireBase(HttpSession session){
    //     List<SalarySlipDto> salarySlipDtos=sala
        
    // }

}