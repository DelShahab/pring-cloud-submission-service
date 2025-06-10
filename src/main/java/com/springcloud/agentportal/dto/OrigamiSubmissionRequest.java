package com.springcloud.agentportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * Request DTO for Origami API submission creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrigamiSubmissionRequest {
    private String emailId;
    private String agentId;
    private String clientName;
}
