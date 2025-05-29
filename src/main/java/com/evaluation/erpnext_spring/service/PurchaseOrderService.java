package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.evaluation.erpnext_spring.dto.orders.PurchaseOrderListResponse;
import com.evaluation.erpnext_spring.dto.purchase_orders.PcoListResponse;
import com.evaluation.erpnext_spring.dto.purchase_orders.PcoMessage;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;

@Service
public class PurchaseOrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    public PurchaseOrderListResponse getPurchaseOrdersBySupplier(HttpSession session, String supplierId, int page, int size) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        int offset = page * size;

        String fields = "[\"name\",\"creation\",\"modified\",\"supplier\",\"supplier_name\",\"order_confirmation_no\"," +
                "\"total_qty\",\"base_total\",\"base_grand_total\",\"grand_total\",\"rounded_total\",\"status\",\"transaction_date\"]";

        String filters = String.format("[[\"supplier\", \"=\", \"%s\"]]", supplierId);

        String url = String.format("%s/api/resource/Purchase Order?filters=%s&fields=%s&limit_start=%d&limit_page_length=%d",
                erpnextApiUrl, filters, fields,  offset,size);

        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<PurchaseOrderListResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    PurchaseOrderListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch purchase orders: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching purchase orders: " + e.getMessage(), e);
        }
    }


    public PurchaseOrderListResponse getPurchaseOrdersBySupplierAndStatus(HttpSession session, String supplierId, String status) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }
    
        String fields = "[\"name\",\"creation\",\"modified\",\"supplier\",\"supplier_name\",\"order_confirmation_no\"," +
                "\"total_qty\",\"base_total\",\"base_grand_total\",\"grand_total\",\"rounded_total\",\"status\",\"transaction_date\"]";
    
        
        String filters = String.format("[[\"supplier\", \"=\", \"%s\"]]", supplierId);
    
        
        if (status != null && !status.isEmpty()) {
            filters = String.format("[[\"supplier\", \"=\", \"%s\"], [\"status\", \"=\", \"%s\"]]", supplierId, status);
        }
    
        
        String url = String.format("%s/api/resource/Purchase Order?filters=%s&fields=%s", 
                                   erpnextApiUrl, filters, fields);
    
        System.out.println(url);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
    
        HttpEntity<String> request = new HttpEntity<>(headers);
    
        try {
            ResponseEntity<PurchaseOrderListResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    PurchaseOrderListResponse.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch purchase orders: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching purchase orders: " + e.getMessage(), e);
        }
    }



    @SuppressWarnings({ "null", "deprecation" })
    public PcoListResponse getOrdersByStatus(HttpSession session, String status, String supplierName) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

       

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(erpnextApiUrl)
                .path("/api/method/erpnext.eval.purchase_order.get_orders_by_status");

        if (supplierName != null && !supplierName.isEmpty()) {
            builder.queryParam("supplier_name", supplierName);
        }
        
        if (status != null && !status.isEmpty()) {
            builder.queryParam("status", status);
        }

        String url=builder.toUriString().replace("%20", " ");
       
        System.out.println(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<PcoMessage> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    PcoMessage.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                
                PcoListResponse responseBody = response.getBody().getMessage();
                if (responseBody == null) {
                    throw new RuntimeException("Empty response body from ERPNext API");
                }
                return responseBody;
            } else {
                throw new RuntimeException("Failed to fetch PCOs. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error while communicating with ERPNext API: " + e.getMessage(), e);
        }
    }
    


}
