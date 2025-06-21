package com.evaluation.erpnext_spring.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.structures.StructureAssignement;
import com.evaluation.erpnext_spring.dto.structures.StructureAssignementResponse;
import com.evaluation.erpnext_spring.dto.structures.StructureAssignementDetail;

import jakarta.servlet.http.HttpSession;

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

    public void assignSalaryStructure(HttpSession session,StructureAssignement request) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }
        
        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);
        
        HttpEntity<StructureAssignement> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec d’assignation pour " + request.getEmployee());
        }
    }   

    public void assignSalaryStructureBloc(HttpSession session,List<StructureAssignement> structureAssignements){
        for (StructureAssignement structureAssignement : structureAssignements) {
            assignSalaryStructure(session, structureAssignement);
        }
    }


    public void assignToSalaryStructures(HttpSession session,String salary_structure,String company,String from_date,String currency,List<StructureAssignementDetail> structureDetails){
        List<StructureAssignement> structureAssignements=new ArrayList<>();
        for (StructureAssignementDetail structureDetail : structureDetails) {
            structureAssignements.add(new StructureAssignement(company, salary_structure, currency, structureDetail.getEmployee(), from_date, structureDetail.getBase(), structureDetail.getVariable(), currency, currency, currency));
        }
        assignSalaryStructureBloc(session, structureAssignements);
    }


    public StructureAssignement getLastStructureAssignementBeforeDate(HttpSession session, String employeeId, String beforeDate) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment?fields=[\"*\"]"
                + "&filters=[[\"employee\",\"=\",\"" + employeeId + "\"],[\"from_date\",\"<=\",\"" + beforeDate + "\"]]"
                + "&order_by=from_date desc&limit_page_length=1";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<StructureAssignementResponse> response = restTemplate
                .exchange(url, org.springframework.http.HttpMethod.GET, entity, StructureAssignementResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().getData().isEmpty()) {
            return response.getBody().getData().get(0);
        }

        return null;  
    }



    public void cancelOrDeleteStructureAssignment(
        HttpSession session,
        String assignmentName,
        boolean deleteIfPossible) {

        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment/" + assignmentName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        if (deleteIfPossible) {
             
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de la suppression de l'attribution : " + assignmentName);
            }

        } else {
            
            Map<String, Object> cancelPayload = new HashMap<>();
            cancelPayload.put("docstatus", 2);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(cancelPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de l'annulation de l'attribution : " + assignmentName);
            }
        }
    }



}
