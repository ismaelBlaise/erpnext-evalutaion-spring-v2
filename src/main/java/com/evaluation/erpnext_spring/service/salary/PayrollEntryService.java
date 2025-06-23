package com.evaluation.erpnext_spring.service.salary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.payrolls.PayrollEmployeeDetail;
import com.evaluation.erpnext_spring.dto.payrolls.PayrollEntryDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpSession;

@Service
public class PayrollEntryService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    /**
     * Crée, valide un Payroll Entry et génère les Salary Slips associés
     */
    public void createPayrollEntryFromDetails(HttpSession session,
                                              String company,
                                              String posting_date,
                                              String currency,
                                              String costCenter,
                                              String payroll_frequency,
                                              String payroll_payable_account,
                                              String start_date,
                                              String end_date,
                                              boolean draft,
                                              List<PayrollEmployeeDetail> employeeDetails) {

        // Convertir "true"/"false" en "Yes"/"No" pour ERPNext
        for (PayrollEmployeeDetail detail : employeeDetails) {
            String withheld = detail.getIs_salary_withheld();
            detail.setIs_salary_withheld("true".equalsIgnoreCase(withheld) ? "Yes" : "No");
        }

        // Préparer le DTO
        PayrollEntryDto payrollEntry = new PayrollEntryDto();
        payrollEntry.setCompany(company);
        payrollEntry.setPosting_date(posting_date);
        payrollEntry.setCurrency(currency);
        payrollEntry.setCost_center(costCenter);
        payrollEntry.setPayroll_frequency(payroll_frequency);
        payrollEntry.setPayroll_payable_account(payroll_payable_account);
        payrollEntry.setStart_date(start_date);
        payrollEntry.setEnd_date(end_date);
        payrollEntry.setDraft(draft);
        payrollEntry.setEmployees(employeeDetails);

        // Étape 1 : créer le payroll entry
        String payrollEntryName = createPayrollEntry(session, payrollEntry);

        // Étape 2 : valider le payroll entry
        submitPayrollEntry(session, payrollEntryName);

        // Étape 3 : générer les salary slips
        // createSalarySlips(session, payrollEntryName);
    }

    /**
     * Crée un Payroll Entry et retourne son nom
     */
    public String createPayrollEntry(HttpSession session, PayrollEntryDto request) {
        String sid = getSessionId(session);
        String url = erpnextApiUrl + "/api/resource/Payroll Entry";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<PayrollEntryDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de création de la Payroll Entry");
        }

        return extractNameFromJson(response.getBody());
    }



    public void submitPayrollEntry(HttpSession session, String payrollEntryName) {
        String sid = getSessionId(session);
        String getUrl = erpnextApiUrl + "/api/resource/Payroll Entry/" + payrollEntryName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);

        ResponseEntity<String> getResponse = restTemplate.exchange(
            getUrl,
            HttpMethod.GET,
            getEntity,
            String.class
        );

        if (!getResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de récupération de la Payroll Entry : " + payrollEntryName);
        }

        try {
            // On lit le corps JSON avec Jackson
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(getResponse.getBody());
            JsonNode docNode = root.get("data"); // correspond au contenu de la Payroll Entry

            // Crée le corps pour la requête POST
            ObjectNode submitBody = mapper.createObjectNode();
            submitBody.set("doc", docNode);

            HttpEntity<String> postEntity = new HttpEntity<>(mapper.writeValueAsString(submitBody), headers);

            String submitUrl = erpnextApiUrl + "/api/method/frappe.client.submit";

            ResponseEntity<String> postResponse = restTemplate.postForEntity(submitUrl, postEntity, String.class);

            if (!postResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de validation (submit) de la Payroll Entry : " + payrollEntryName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement JSON pour submit : " + e.getMessage(), e);
        }
    }




    /**
     * Génère les Salary Slips depuis un Payroll Entry
     */
    public void createSalarySlips(HttpSession session, String payrollEntryName) {
        String sid = getSessionId(session);
        String url = erpnextApiUrl + "/api/method/erpnext.payroll.doctype.payroll_entry.payroll_entry.create_salary_slips";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);

        String jsonBody = "{\"payroll_entry\":\"" + payrollEntryName + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de création des Salary Slips");
        }
    }

    /**
     * Extrait le champ "name" d'une réponse JSON
     */
    private String extractNameFromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            return root.path("data").path("name").asText();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'extraction du nom du Payroll Entry", e);
        }
    }

    /**
     * Récupère le SID depuis la session, sinon lève une exception
     */
    private String getSessionId(HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }
        return sid;
    }
}
