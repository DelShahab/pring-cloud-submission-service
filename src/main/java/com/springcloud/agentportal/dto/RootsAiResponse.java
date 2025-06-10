package com.springcloud.agentportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO from Roots.ai API with parsed ACORD file data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RootsAiResponse {
    private String requestId;
    private String status;
    private Map<String, Object> parsedData;
    private String message;
}
