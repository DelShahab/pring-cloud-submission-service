package com.windsurf.agentportal.client;

import com.windsurf.agentportal.dto.OrigamiSubmissionRequest;
import com.windsurf.agentportal.dto.OrigamiSubmissionResponse;
import com.windsurf.agentportal.dto.OrigamiUpdateRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for Origami API interactions
 */
@FeignClient(name = "origamiClient", url = "${api.origami.base-url}")
public interface OrigamiClient {

    /**
     * Creates a new submission in Origami
     *
     * @param apiKey API key for authentication
     * @param request Request data containing email and other details
     * @return Response with submissionProposalId
     */
    @PostMapping("/api/submissions")
    OrigamiSubmissionResponse createSubmission(
        @RequestHeader("X-API-KEY") String apiKey,
        @RequestBody OrigamiSubmissionRequest request
    );
    
    /**
     * Updates an existing submission with parsed data
     *
     * @param apiKey API key for authentication
     * @param submissionProposalId ID of the submission to update
     * @param request Request containing the parsed data
     * @return Updated submission response
     */
    @PutMapping("/api/submissions/{submissionProposalId}")
    OrigamiSubmissionResponse updateSubmission(
        @RequestHeader("X-API-KEY") String apiKey,
        @PathVariable("submissionProposalId") String submissionProposalId,
        @RequestBody OrigamiUpdateRequest request
    );
}
