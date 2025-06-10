package com.springcloud.agentportal.controller;

import com.springcloud.agentportal.dto.NotificationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling notification requests
 * This endpoint is intentionally open (no API key required)
 */
@RestController
@RequestMapping("/notifyme")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification API", description = "API for handling notifications")
public class NotificationController {

    @Operation(summary = "Receive notification for user",
            description = "Endpoint for receiving notifications about submissions. This endpoint is used for demonstration and isn't normally part of this service (it would be in the Agent Portal).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification received successfully", 
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<Void> receiveNotification(
            @PathVariable String userId,
            @RequestBody NotificationRequest notification) {

        log.info("Received notification for user {}: submission {}, status {}", 
                userId, notification.getSubmissionId(), notification.getStatus());
        
        // In a real application, this would store the notification or push it to the client
        // This endpoint simulates the Agent Portal's notification receiver
        
        return ResponseEntity.ok().build();
    }
}
