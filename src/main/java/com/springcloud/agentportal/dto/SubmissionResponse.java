package com.springcloud.agentportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * DTO for sending responses back to the caller
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    
    private String submissionId;
    private String status;
    private String message;
    
    // Static factory methods for common responses
    public static SubmissionResponse success(String submissionId) {
        return SubmissionResponse.builder()
                .submissionId(submissionId)
                .status("success")
                .message("Submission successfully processed")
                .build();
    }
    
    public static SubmissionResponse error(String message) {
        return SubmissionResponse.builder()
                .status("error")
                .message(message)
                .build();
    }
}
