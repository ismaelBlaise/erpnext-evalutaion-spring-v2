package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.quotations.SupplierQuotationListResponse;
import com.evaluation.erpnext_spring.dto.supplier_quotations.SpqListResponse;
import com.evaluation.erpnext_spring.dto.supplier_quotations.SpqMessage;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupplierQuotationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    
    public SupplierQuotationListResponse getQuotationsBySupplier(HttpSession session, String supplierId, int page, int size) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        int offset = page * size;

        String fields = "[\"name\",\"creation\",\"modified\",\"supplier\",\"supplier_name\",\"quotation_number\",\"total\",\"grand_total\",\"status\",\"transaction_date\"]";

        String filters = String.format("[[\"supplier_name\", \"=\", \"%s\"]]", supplierId);
        
        String url = String.format("%s/api/resource/Supplier Quotation?filters=%s&fields=%s&limit_start=%d&limit_page_length=%d", 
                           erpnextApiUrl, filters, fields, offset,size);


        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SupplierQuotationListResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    SupplierQuotationListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch supplier quotations: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching supplier quotations: " + e.getMessage(), e);
        }
    }



    @SuppressWarnings("null")
    public SpqListResponse getSupplierQuotationByRqf(HttpSession session, String rqf, int page, int pageSize) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String url = String.format("%s/api/method/erpnext.eval.supplier_quotation.get_supplier_quotations_by_rfq?request_for_quotation_name=%s&page=%d&page_size=%d",
                erpnextApiUrl, 
                rqf,  
                page,
                pageSize
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SpqMessage> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                SpqMessage.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getMessage();
            } else {
                throw new RuntimeException("Failed to fetch SPQs: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching SPQs: " + e.getMessage(), e);
        }
    }


    
    @SuppressWarnings({ "null", "unchecked", "rawtypes" })
    public Map<String, Object> validateSupplierQuotation(HttpSession session, String quotationName) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }
    
        String url =erpnextApiUrl+"/api/resource/Supplier Quotation/"+quotationName;
        
        System.out.println(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("run_method", "submit");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
    
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (Map<String, Object>) response.getBody().get("data");
            } else {
                throw new RuntimeException("Failed to validate supplier quotation. Status code: " + 
                                        response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while validating supplier quotation: " + e.getMessage(), e);
        }
    }

}