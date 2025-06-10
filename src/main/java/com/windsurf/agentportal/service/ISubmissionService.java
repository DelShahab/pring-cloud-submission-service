package com.windsurf.agentportal.service;

import com.windsurf.agentportal.dto.SubmissionRequest;
import com.windsurf.agentportal.dto.SubmissionResponse;
import com.windsurf.agentportal.model.Submission;
import com.windsurf.agentportal.service.base.BaseService;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing submissions
 */
public interface ISubmissionService extends BaseService<Submission, String> {
    
    /**
     * Process a new submission with ACORD file
     *
     * @param request Submission request with required parameters
     * @param acordFile ACORD file to be processed
     * @return Submission response with details
     */
    SubmissionResponse processSubmission(SubmissionRequest request, MultipartFile acordFile);
    
    /**
     * Get submissions by user ID
     * @param userId User ID
     * @return List of submissions
     */
    List<Submission> getSubmissionsByUserId(String userId);
    
    /**
     * Get submissions by agent ID
     * @param agentId Agent ID
     * @return List of submissions
     */
    List<Submission> getSubmissionsByAgentId(String agentId);
    
    /**
     * Get submissions by status
     * @param status Status
     * @return List of submissions
     */
    List<Submission> getSubmissionsByStatus(String status);
}
