package com.windsurf.agentportal.service.impl;

import com.windsurf.agentportal.client.OrigamiClient;
import com.windsurf.agentportal.client.RootsAiClient;
import com.windsurf.agentportal.dto.*;
import com.windsurf.agentportal.exception.SubmissionServiceException;
import com.windsurf.agentportal.model.Submission;
import com.windsurf.agentportal.repository.SubmissionRepository;
import com.windsurf.agentportal.service.ISubmissionService;
import com.windsurf.agentportal.service.base.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of submission service that orchestrates calls to external APIs
 * and notifies the Agent Portal about submission status
 */
@Service
@Slf4j
public class SubmissionServiceImpl extends BaseServiceImpl<Submission, String, SubmissionRepository>
        implements ISubmissionService {

    private final OrigamiClient origamiClient;
    private final RootsAiClient rootsAiClient;
    private final CircuitBreakerSubmissionService circuitBreakerSubmissionService;

    @Value("${api.origami.api-key}")
    private String origamiApiKey;

    @Value("${api.rootsai.api-key}")
    private String rootsAiApiKey;

    @Value("${api.agent-portal.api-key}")
    private String agentPortalApiKey;

    public SubmissionServiceImpl(SubmissionRepository repository,
                               OrigamiClient origamiClient,
                               RootsAiClient rootsAiClient,
                               CircuitBreakerSubmissionService circuitBreakerSubmissionService) {
        super(repository);
        this.origamiClient = origamiClient;
        this.rootsAiClient = rootsAiClient;
        this.circuitBreakerSubmissionService = circuitBreakerSubmissionService;
    }

    @Override
    public SubmissionResponse processSubmission(SubmissionRequest request, MultipartFile acordFile) {
        log.info("Processing new submission for email: {}, userId: {}", request.getEmailId(), request.getUserId());
        
        try {
            // Step 1: Create submission in our database
            Submission submission = Submission.builder()
                    .id(UUID.randomUUID().toString())
                    .emailId(request.getEmailId())
                    .userId(request.getUserId())
                    .agentId(request.getAgentId())
                    .clientName(request.getClientName())
                    .status("PROCESSING")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            repository.save(submission);
            
            // Step 2: Create submission in Origami
            OrigamiSubmissionRequest origamiRequest = OrigamiSubmissionRequest.builder()
                    .emailId(request.getEmailId())
                    .agentId(request.getAgentId())
                    .clientName(request.getClientName())
                    .build();
            
            log.debug("Calling Origami API to create submission with data: {}", origamiRequest);
            OrigamiSubmissionResponse origamiResponse = origamiClient.createSubmission(
                    origamiApiKey, origamiRequest);
            
            String submissionProposalId = origamiResponse.getSubmissionProposalId();
            log.info("Obtained submissionProposalId: {} from Origami", submissionProposalId);
            
            // Update our submission with the submissionProposalId
            submission.setSubmissionProposalId(submissionProposalId);
            repository.save(submission);
            
            // Step 3: Send ACORD file to Roots.ai for parsing
            log.debug("Sending ACORD file to Roots.ai for parsing");
            RootsAiResponse rootsAiResponse = rootsAiClient.parseAcordFile(
                    rootsAiApiKey, acordFile);
            
            log.info("Received parsed data from Roots.ai with requestId: {}", rootsAiResponse.getRequestId());
            
            // Step 4: Update Origami submission with parsed data
            OrigamiUpdateRequest updateRequest = OrigamiUpdateRequest.builder()
                    .submissionProposalId(submissionProposalId)
                    .parsedData(rootsAiResponse.getParsedData())
                    .build();
            
            log.debug("Updating Origami submission with parsed data");
            origamiClient.updateSubmission(
                    origamiApiKey, submissionProposalId, updateRequest);
            
            // Update our submission with parsed data and status
            submission.setParsedData(rootsAiResponse.getParsedData());
            submission.setStatus("PROCESSED");
            submission.setUpdatedAt(LocalDateTime.now());
            repository.save(submission);
            
            // Step 5: Send notification to Agent Portal using circuit breaker pattern
            log.debug("Sending notification to Agent Portal for userId: {}", request.getUserId());
            circuitBreakerSubmissionService.notifyUserWithCircuitBreaker(
                    request.getUserId(),
                    submissionProposalId,
                    "PROCESSED",
                    "Submission was successfully processed");
            
            log.info("Submission processing completed successfully for submissionId: {}", submissionProposalId);
            
            return SubmissionResponse.success(submissionProposalId);
            
        } catch (Exception e) {
            log.error("Error processing submission: {}", e.getMessage(), e);
            throw new SubmissionServiceException("Failed to process submission: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Submission> getSubmissionsByUserId(String userId) {
        log.debug("Fetching submissions for userId: {}", userId);
        return repository.findByUserId(userId);
    }
    
    @Override
    public List<Submission> getSubmissionsByAgentId(String agentId) {
        log.debug("Fetching submissions for agentId: {}", agentId);
        return repository.findByAgentId(agentId);
    }
    
    @Override
    public List<Submission> getSubmissionsByStatus(String status) {
        log.debug("Fetching submissions with status: {}", status);
        return repository.findByStatus(status);
    }
}
