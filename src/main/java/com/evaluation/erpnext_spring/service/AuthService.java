package com.evaluation.erpnext_spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.auth.ERPNextAuthResponse;
import com.evaluation.erpnext_spring.dto.auth.LoginRequestDTO;
import com.evaluation.erpnext_spring.dto.auth.LoginResponseDTO;

import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    
    private final RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("null")
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        String loginUrl = erpnextApiUrl + "/api/method/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Create JSON request body
        String requestBody = String.format("{\"usr\":\"%s\",\"pwd\":\"%s\"}", 
            loginRequest.getUsername(), 
            loginRequest.getPassword());

        // System.out.println(requestBody);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<ERPNextAuthResponse> response = restTemplate.postForEntity(
                loginUrl, 
                request, 
                ERPNextAuthResponse.class
            );
            List<String> cookies = response.getHeaders().get("Set-Cookie");
            String sid = extractSidFromCookies(cookies);

            if (sid != null &&  response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ERPNextAuthResponse authResponse = response.getBody();
               
                authResponse.setSid(sid);
                System.out.println("SID: "+authResponse.getSid());
                return new LoginResponseDTO(
                    true, 
                    "Login successful", 
                    authResponse.getSid(), 
                    authResponse.getFullName()
                );
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new LoginResponseDTO(false, "Invalid username or password");
            }
            return new LoginResponseDTO(false, "Login failed: " + e.getMessage());
        } catch (Exception e) {
            return new LoginResponseDTO(false, "An error occurred during login: " + e.getMessage());
        }

        return new LoginResponseDTO(false, "Login failed with unknown error");
    }

    private String extractSidFromCookies(List<String> cookies) {
        if (cookies == null) return null;
        
        for (String cookie : cookies) {
            if (cookie.contains("sid=")) {
                
                String[] parts = cookie.split(";")[0].split("=");
                if (parts.length > 1) {
                    return parts[1];
                }
            }
        }
        return null;
    }
}