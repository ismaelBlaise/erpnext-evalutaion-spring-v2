package com.evaluation.erpnext_spring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.structures.StructureAssignement;
import com.evaluation.erpnext_spring.dto.structures.StructureAssignementResponse;
import com.evaluation.erpnext_spring.dto.structures.StructureDetail;

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


    public void assignToSalaryStructures(HttpSession session,String salary_structure,String company,String from_date,String currency,List<StructureDetail> structureDetails){
        List<StructureAssignement> structureAssignements=new ArrayList<>();
        for (StructureDetail structureDetail : structureDetails) {
            structureAssignements.add(new StructureAssignement(company, salary_structure, currency, structureDetail.getEmployee(), from_date, structureDetail.getBase(), structureDetail.getVariable()));
        }
        assignSalaryStructureBloc(session, structureAssignements);
    }


    public StructureAssignement getLastStructureAssignementBeforeDate(HttpSession session, String employeeId, String beforeDate) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment?fields=[\"name\",\"employee\",\"salary_structure\",\"from_date\"]"
                + "&filters=[[\"employee\",\"=\",\"" + employeeId + "\"],[\"from_date\",\"<\",\"" + beforeDate + "\"]]"
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

}
