package com.windsurf.agentportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for handling submission requests from Agent Portal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    
    @NotNull
    @NotBlank
    @Email
    private String emailId;
    
    @NotNull
    @NotBlank
    private String userId;
    
    // Additional fields that might be part of the request
    private String agentId;
    private String clientName;
}
