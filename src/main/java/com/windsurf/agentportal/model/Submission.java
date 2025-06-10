package com.windsurf.agentportal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.Convert;
import jakarta.persistence.AttributeConverter;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Submission entity representing an insurance submission
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Submission {
    
    @Id
    private String id;
    private String emailId;
    private String userId;
    private String agentId;
    private String clientName;
    private String submissionProposalId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Lob
    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonToMapConverter.class)
    private Map<String, Object> parsedData;
}

/**
 * JPA Converter to convert between JSON string and Map for parsedData
 */
class JsonToMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData)) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to map", e);
        }
    }
}
