package com.windsurf.agentportal.service;

import com.windsurf.agentportal.client.AgentPortalNotifierClient;
import com.windsurf.agentportal.client.OrigamiClient;
import com.windsurf.agentportal.client.RootsAiClient;
import com.windsurf.agentportal.dto.*;
import com.windsurf.agentportal.exception.SubmissionServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for handling submission processing
 * Orchestrates calls to external APIs and notification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final OrigamiClient origamiClient;
    private final RootsAiClient rootsAiClient;
    private final AgentPortalNotifierClient agentPortalNotifierClient;

    @Value("${api.origami.api-key}")
    private String origamiApiKey;

    @Value("${api.rootsai.api-key}")
    private String rootsAiApiKey;

    @Value("${api.agent-portal.api-key}")
    private String agentPortalApiKey;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Process a new submission with ACORD file
     *
     * @param request Submission request with required parameters
     * @param acordFile ACORD file to be processed
     * @return Submission response with details
     */
    public SubmissionResponse processSubmission(SubmissionRequest request, MultipartFile acordFile) {
        log.info("Processing new submission for email: {}, userId: {}", request.getEmailId(), request.getUserId());
        
        try {
            // Step 1: Create submission in Origami
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
            
            // Step 2: Send ACORD file to Roots.ai for parsing
            log.debug("Sending ACORD file to Roots.ai for parsing");
            RootsAiResponse rootsAiResponse = rootsAiClient.parseAcordFile(
                    rootsAiApiKey, acordFile);
            
            log.info("Received parsed data from Roots.ai with requestId: {}", rootsAiResponse.getRequestId());
            
            // Step 3: Update Origami submission with parsed data
            OrigamiUpdateRequest updateRequest = OrigamiUpdateRequest.builder()
                    .submissionProposalId(submissionProposalId)
                    .parsedData(rootsAiResponse.getParsedData())
                    .build();
            
            log.debug("Updating Origami submission with parsed data");
            origamiClient.updateSubmission(
                    origamiApiKey, submissionProposalId, updateRequest);
            
            // Step 4: Send notification to Agent Portal
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .submissionId(submissionProposalId)
                    .status("PROCESSED")
                    .message("Submission was successfully processed")
                    .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                    .build();
            
            log.debug("Sending notification to Agent Portal for userId: {}", request.getUserId());
            agentPortalNotifierClient.notifyUser(
                    agentPortalApiKey, request.getUserId(), notificationRequest);
            
            log.info("Submission processing completed successfully for submissionId: {}", submissionProposalId);
            
            return SubmissionResponse.success(submissionProposalId);
            
        } catch (Exception e) {
            log.error("Error processing submission: {}", e.getMessage(), e);
            throw new SubmissionServiceException("Failed to process submission: " + e.getMessage(), e);
        }
    }
}
