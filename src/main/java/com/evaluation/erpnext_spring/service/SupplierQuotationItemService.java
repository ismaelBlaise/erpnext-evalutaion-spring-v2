package com.evaluation.erpnext_spring.service;

import com.evaluation.erpnext_spring.dto.quotations.SupplierQuotationItemListResponse;
import com.evaluation.erpnext_spring.dto.quotations.UpdateItemRateResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupplierQuotationItemService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    public SupplierQuotationItemListResponse getSupplierQuotation(HttpSession session, String quotationId) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        
        String url = String.format("%s/api/resource/Supplier Quotation/%s", erpnextApiUrl, quotationId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SupplierQuotationItemListResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    SupplierQuotationItemListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch supplier quotation: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching supplier quotation: " + e.getMessage(), e);
        }
    }




    // @SuppressWarnings("null")
    // public UpdateItemRateResponseDTO updateSupplierQuotationItemRate(HttpSession session, String itemName, double newRate) {
    //     String sid = (String) session.getAttribute("sid");
    //     if (sid == null || sid.isEmpty()) {
    //         throw new RuntimeException("Session not authenticated");
    //     }

    //     String apiUrl = String.format("%s/api/method/erpnext.eval.update_price.update_supplier_quotation_item_rate", erpnextApiUrl);

        
    //     @SuppressWarnings("deprecation")
    //     UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
    //             .queryParam("item_name", itemName)
    //             .queryParam("new_rate", newRate);

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    //     headers.add("Cookie", "sid=" + sid);

    //     HttpEntity<String> request = new HttpEntity<>(headers);

    //     try {
    //         ResponseEntity<UpdateItemRateResponseGroup> response = restTemplate.exchange(
    //                 builder.toUriString(),
    //                 HttpMethod.POST,
    //                 request,
    //                 UpdateItemRateResponseGroup.class
    //         );

    //         if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
    //             return response.getBody().getMessage();
    //         } else {
    //             return new UpdateItemRateResponseDTO("error", "Erreur HTTP: " + response.getStatusCode());
    //         }
    //     } catch (Exception e) {
    //         throw new RuntimeException("Erreur lors de la mise à jour du prix: " + e.getMessage(), e);
    //     }
    // }


    @SuppressWarnings({ "null", "unchecked" })
    public UpdateItemRateResponseDTO updateSupplierQuotationItemRate(HttpSession session, String itemName, double newRate) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

       
        String apiUrl = String.format("%s/api/resource/Supplier Quotation Item/%s", erpnextApiUrl, itemName);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("rate", newRate);

       
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateFields, headers);

        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.PUT,
                    request,
                    Map.class 
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unused")
                Map<String, Object> body = response.getBody();
                return new UpdateItemRateResponseDTO("success", "Mise a jour reussie");
            } else {
                return new UpdateItemRateResponseDTO("error", "Erreur HTTP: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du prix: " + e.getMessage(), e);
        }
    }

}
