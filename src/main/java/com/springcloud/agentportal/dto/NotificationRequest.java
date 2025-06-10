package com.springcloud.agentportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * DTO for sending notifications to Agent Portal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String submissionId;
    private String status;
    private String message;
    private String timestamp;
}
