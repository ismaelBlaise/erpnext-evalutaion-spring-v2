package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.invoices.PurchaseInvoice;
import com.evaluation.erpnext_spring.dto.invoices.PurchaseInvoiceListResponse;
import com.evaluation.erpnext_spring.dto.invoices.PurchaseInvoiceResponse;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;

@Service
public class PurchaseInvoiceService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

    public PurchaseInvoiceListResponse getPurchaseInvoices(HttpSession session, int page, int size) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        int offset = page * size;

        String fields = "[\"name\",\"title\",\"supplier\",\"supplier_name\",\"supplier_address\",\"contact_person\"," +
                "\"contact_email\",\"contact_mobile\",\"company\",\"posting_date\",\"due_date\",\"bill_date\"," +
                "\"currency\",\"grand_total\",\"base_grand_total\",\"net_total\",\"base_total\"," +
                "\"base_total_taxes_and_charges\",\"total_taxes_and_charges\",\"paid_amount\",\"outstanding_amount\"," +
                "\"status\",\"is_paid\",\"is_return\",\"return_against\",\"amended_from\",\"tax_id\",\"tax_category\"," +
                "\"tax_withholding_category\",\"mode_of_payment\",\"payment_terms_template\",\"cost_center\",\"project\"," +
                "\"update_stock\",\"shipping_address\",\"dispatch_address\",\"remarks\",\"terms\"]";

        String url = String.format("%s/api/resource/Purchase Invoice?fields=%s&limit_start=%d&limit_page_length=%d",
                erpnextApiUrl, fields, offset,size);

        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<PurchaseInvoiceListResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    PurchaseInvoiceListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch purchase invoices: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching purchase invoices: " + e.getMessage(), e);
        }
    }


    public PurchaseInvoiceListResponse getPurchaseInvoicesByStatus(HttpSession session, String status) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String fields = "[\"name\",\"title\",\"supplier\",\"supplier_name\",\"supplier_address\",\"contact_person\"," +
                "\"contact_email\",\"contact_mobile\",\"company\",\"posting_date\",\"due_date\",\"bill_date\"," +
                "\"currency\",\"grand_total\",\"base_grand_total\",\"net_total\",\"base_total\"," +
                "\"base_total_taxes_and_charges\",\"total_taxes_and_charges\",\"paid_amount\",\"outstanding_amount\"," +
                "\"status\",\"is_paid\",\"is_return\",\"return_against\",\"amended_from\",\"tax_id\",\"tax_category\"," +
                "\"tax_withholding_category\",\"mode_of_payment\",\"payment_terms_template\",\"cost_center\",\"project\"," +
                "\"update_stock\",\"shipping_address\",\"dispatch_address\",\"remarks\",\"terms\"]";

        StringBuilder urlBuilder = new StringBuilder(String.format("%s/api/resource/Purchase Invoice?fields=%s",
                erpnextApiUrl, fields));

        if (status != null && !status.isEmpty()) {
            String filters = String.format("[[\"Purchase Invoice\",\"status\",\"=\",\"%s\"]]", status);
            urlBuilder.append("&filters=").append(filters); // encodage des guillemets
        }

        String url = urlBuilder.toString();

        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<PurchaseInvoiceListResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    PurchaseInvoiceListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch purchase invoices: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching purchase invoices: " + e.getMessage(), e);
        }
    }


    @SuppressWarnings("null")
    public PurchaseInvoice getPurchaseInvoiceByName(HttpSession session, String invoiceName) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String url = String.format("%s/api/resource/Purchase Invoice/%s", 
                erpnextApiUrl, invoiceName);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<PurchaseInvoiceResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    PurchaseInvoiceResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getData();
            } else {
                throw new RuntimeException("Failed to fetch purchase invoice. Status: " + 
                    response.getStatusCode() + ", Response: " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("ERPNext API error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching purchase invoice: " + e.getMessage(), e);
        }
    }
}
