package com.springcloud.agentportal.client;

import com.springcloud.agentportal.dto.RootsAiResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Feign client for Roots.ai API interactions
 * Handles ACORD file parsing
 */
@FeignClient(name = "rootsAiClient", url = "${app.apis.rootsai.base-url}")
public interface RootsAiClient {

    /**
     * Sends ACORD file to Roots.ai for parsing
     *
     * @param apiKey API key for authentication
     * @param file ACORD file to be parsed
     * @return Parsed data response
     */
    @PostMapping(value = "/api/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    RootsAiResponse parseAcordFile(
        @RequestHeader("X-API-KEY") String apiKey,
        @RequestPart("file") MultipartFile file
    );
}
