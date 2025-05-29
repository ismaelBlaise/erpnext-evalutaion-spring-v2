package com.evaluation.erpnext_spring.service;

import com.evaluation.erpnext_spring.dto.requests_for_quotation.RfqListResponse;
import com.evaluation.erpnext_spring.dto.requests_for_quotation.RfqMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;

@Service
public class RequestForQuotationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    @SuppressWarnings("null")
    public RfqListResponse getRequestsForQuotationBySupplier(HttpSession session, String supplierName, int page, int pageSize) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String url = String.format("%s/api/method/erpnext.eval.request_for_quotation.get_supplier_request_for_quotations?supplier_name=%s&page=%d&page_size=%d",
                erpnextApiUrl, 
                supplierName,  
                page,
                pageSize
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<RfqMessage> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                RfqMessage.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getMessage();
            } else {
                throw new RuntimeException("Failed to fetch RFQs: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching RFQs: " + e.getMessage(), e);
        }
    }
}
