package com.springcloud.agentportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO from Origami API for submission creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrigamiSubmissionResponse {
    private String submissionProposalId;
    private String status;
    private String message;
}
