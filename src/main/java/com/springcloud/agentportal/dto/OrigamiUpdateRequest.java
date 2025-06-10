package com.springcloud.agentportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Map;

/**
 * DTO for updating Origami submission with parsed data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrigamiUpdateRequest {
    private String submissionProposalId;
    private Map<String, Object> parsedData;
}
