package com.windsurf.agentportal.config;

import com.windsurf.agentportal.exception.SubmissionServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Custom error decoder for Feign clients that converts HTTP errors to application exceptions
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();
        
        // Read response body
        String responseBody = getResponseBody(response);
        log.error("Error calling {}. Status: {}. Response: {}", requestUrl, status, responseBody);

        // Handle specific error status codes
        if (status >= 400 && status < 500) {
            return new SubmissionServiceException(
                    String.format("Client error when calling %s: %s - %s", 
                            requestUrl, HttpStatus.valueOf(status), responseBody));
        } else if (status >= 500) {
            return new SubmissionServiceException(
                    String.format("Server error when calling %s: %s - %s", 
                            requestUrl, HttpStatus.valueOf(status), responseBody));
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }

    private String getResponseBody(Response response) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.warn("Failed to read response body", e);
            return "Unable to read response body";
        }
    }
}
