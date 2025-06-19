package com.evaluation.erpnext_spring.dto.auth;

import lombok.Data;

@Data
public class LoginResponseDto {
    private boolean success;
    private String message;
    private String sessionId;
    private String fullName;
    
    public LoginResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponseDto(boolean success, String message, String sessionId, String fullName) {
        this.success = success;
        this.message = message;
        this.sessionId = sessionId;
        this.fullName = fullName;
    }


}