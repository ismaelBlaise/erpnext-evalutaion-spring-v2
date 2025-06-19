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
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.salaries.SalaryDeduction;
import com.evaluation.erpnext_spring.dto.salaries.SalaryEarning;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDetail;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;

import jakarta.servlet.http.HttpSession;

public class FicheDePaieService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    @Autowired
    private SalarySlipService salarySlipService;


    public List<SalarySlipDto> cancelAndUpdateSalarySlips(HttpSession session,
                                                           List<SalarySlipDto> salarySlipDtos,
                                                           String componentName,
                                                           double percentageChange,
                                                           boolean isIncrease) throws Exception {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        List<SalarySlipDto> updatedSlips = new ArrayList<>();
        HttpHeaders headers = buildHeadersWithSid(sid);

        for (SalarySlipDto slipDto : salarySlipDtos) {
            try {
                SalarySlipDto updatedSlip = cancelAndUpdateSalarySlip(session, headers, slipDto.getName(), componentName, percentageChange, isIncrease);
                updatedSlips.add(updatedSlip);
            } catch (Exception e) {
                throw new Exception("Erreur lors du traitement de la Salary Slip " + slipDto.getName() + " : " + e.getMessage(), e);
            }
        }

        return updatedSlips;
    }

    private SalarySlipDto cancelAndUpdateSalarySlip(HttpSession session,
                                                    HttpHeaders headers,
                                                    String slipName,
                                                    String componentName,
                                                    double percentageChange,
                                                    boolean isIncrease) {

        SalarySlipDetail originalDetail = salarySlipService.getSalarySlipByName(session, slipName);
        SalarySlipDto originalSlip = originalDetail.getData();

        if (originalSlip.getDocStatus() == 1) {
            cancelSalarySlip(headers, originalSlip.getName());
        }

        List<SalaryEarning> modifiedEarnings = modifyEarnings(originalSlip.getEarnings(), componentName, percentageChange, isIncrease);
        List<SalaryDeduction> modifiedDeductions = modifyDeductions(originalSlip.getDeductions(), componentName, percentageChange, isIncrease);

        return createNewSalarySlip(headers, originalSlip, modifiedEarnings, modifiedDeductions);
    }

    private HttpHeaders buildHeadersWithSid(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        return headers;
    }

    private void cancelSalarySlip(HttpHeaders headers, String slipName) {
        String cancelUrl = erpnextApiUrl + "/api/resource/Salary Slip/" + slipName;
        Map<String, Object> cancelPayload = new HashMap<>();
        cancelPayload.put("docstatus", 2);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(cancelPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(cancelUrl, HttpMethod.PUT, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de l'annulation de la fiche de paie : " + slipName);
        }
    }

    private List<SalaryEarning> modifyEarnings(List<SalaryEarning> earnings,
                                               String componentName,
                                               double percentageChange,
                                               boolean isIncrease) {
        List<SalaryEarning> modified = new ArrayList<>();
        if (earnings != null) {
            for (SalaryEarning earning : earnings) {
                double amount = earning.getAmount();
                if (earning.getSalaryComponent().equals(componentName)) {
                    amount = isIncrease
                            ? amount * (1 + percentageChange / 100.0)
                            : amount * (1 - percentageChange / 100.0);
                    amount = Math.round(amount * 100.0) / 100.0;
                }
                earning.setAmount(amount);
                modified.add(earning);
            }
        }
        return modified;
    }

    private List<SalaryDeduction> modifyDeductions(List<SalaryDeduction> deductions,
                                                   String componentName,
                                                   double percentageChange,
                                                   boolean isIncrease) {
        List<SalaryDeduction> modified = new ArrayList<>();
        if (deductions != null) {
            for (SalaryDeduction deduction : deductions) {
                double amount = deduction.getAmount();
                if (deduction.getSalaryComponent().equals(componentName)) {
                    amount = isIncrease
                            ? amount * (1 + percentageChange / 100.0)
                            : amount * (1 - percentageChange / 100.0);
                    amount = Math.round(amount * 100.0) / 100.0;
                }
                deduction.setAmount(amount);
                modified.add(deduction);
            }
        }
        return modified;
    }

    private SalarySlipDto createNewSalarySlip(HttpHeaders headers,
                                              SalarySlipDto originalSlip,
                                              List<SalaryEarning> earnings,
                                              List<SalaryDeduction> deductions) {
        String createUrl = erpnextApiUrl + "/api/resource/Salary Slip";

        Map<String, Object> payload = new HashMap<>();
        payload.put("employee", originalSlip.getEmployee());
        payload.put("start_date", originalSlip.getStartDate());
        payload.put("end_date", originalSlip.getEndDate());
        payload.put("company", originalSlip.getCompany());
        payload.put("posting_date", originalSlip.getPostingDate());
        payload.put("salary_structure", originalSlip.getSalaryStructure());
        payload.put("earnings", earnings);
        payload.put("deductions", deductions);
        payload.put("docstatus", 1);
        payload.put("parent", originalSlip.getName());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<SalarySlipDetail> response = restTemplate.exchange(
                createUrl,
                HttpMethod.POST,
                request,
                SalarySlipDetail.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de la création du nouveau Salary Slip.");
        }

        return response.getBody().getData();
    }


}
