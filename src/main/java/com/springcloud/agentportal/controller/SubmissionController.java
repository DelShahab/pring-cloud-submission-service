package com.springcloud.agentportal.controller;

import com.springcloud.agentportal.dto.SubmissionRequest;
import com.springcloud.agentportal.dto.SubmissionResponse;
import com.springcloud.agentportal.service.SubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

/**
 * Controller for handling submission requests
 */
@RestController
@RequestMapping("/submission")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Submission API", description = "API for handling submission requests from Agent Portal")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "Process a new submission with ACORD file",
            description = "Receives ACORD file and metadata, processes it through Origami and Roots.ai, and notifies the Agent Portal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission processed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmissionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid API key"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionResponse> processSubmission(
            @RequestPart("request") @Valid SubmissionRequest request,
            @RequestPart("file") MultipartFile acordFile) {

        log.info("Received submission request for userId: {} with file size: {} bytes",
                request.getUserId(), acordFile.getSize());

        if (acordFile.isEmpty()) {
            log.warn("Empty file received in submission request");
            return ResponseEntity.badRequest().body(SubmissionResponse.error("ACORD file is empty"));
        }

        SubmissionResponse response = submissionService.processSubmission(request, acordFile);
        return ResponseEntity.ok(response);
    }
}
