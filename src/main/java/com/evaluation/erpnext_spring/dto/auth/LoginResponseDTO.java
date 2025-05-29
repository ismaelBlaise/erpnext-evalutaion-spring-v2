package com.evaluation.erpnext_spring.dto.auth;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private String sessionId;
    private String fullName;
    
    public LoginResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponseDTO(boolean success, String message, String sessionId, String fullName) {
        this.success = success;
        this.message = message;
        this.sessionId = sessionId;
        this.fullName = fullName;
    }


}