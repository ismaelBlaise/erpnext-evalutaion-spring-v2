package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.evaluation.erpnext_spring.dto.payments.PaymentDTO;
import com.evaluation.erpnext_spring.dto.payments.PaymentResponseGroupDTO;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentResponseGroupDTO processPayment(PaymentDTO paymentDTO) {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
        String sid = (String) session.getAttribute("sid");

        if (sid == null) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextApiUrl + "/api/resource/Payment Entry";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
        headers.set("Cookie", "sid=" + sid);

        HttpEntity<PaymentDTO> request = new HttpEntity<>(paymentDTO, headers);

        try {
            ResponseEntity<PaymentResponseGroupDTO> response = restTemplate.postForEntity(url, request, PaymentResponseGroupDTO.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur ERPNext - Code HTTP: " + response.getStatusCode() + " - Message: " + response.getBody());
            }

            return response.getBody();

        } catch (HttpClientErrorException e) {
             
            throw new RuntimeException("Erreur côté client (4xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erreur côté serveur ERPNext (5xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Erreur d’accès au serveur ERPNext: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Erreur lors de l’appel ERPNext: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur inattendue lors du traitement du paiement: " + e.getMessage(), e);
        }
    }



    public String submitPaymentEntry(String paymentEntryName) {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
        String sid = (String) session.getAttribute("sid");
    
        if (sid == null) {
            throw new RuntimeException("Session non authentifiée");
        }
    
        String url = erpnextApiUrl + "/api/resource/Payment Entry/" + paymentEntryName ;
    
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
        headers.set("Cookie", "sid=" + sid);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("run_method", "submit");
    
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
    
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur lors de la validation du Payment Entry - HTTP " + response.getStatusCode() + " - " + response.getBody());
            }
    
            return response.getBody();
    
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erreur côté client (4xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erreur côté serveur ERPNext (5xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Erreur d’accès au serveur ERPNext: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Erreur lors de l’appel ERPNext: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur inattendue lors de la validation du paiement: " + e.getMessage(), e);
        }
    }

    
}
