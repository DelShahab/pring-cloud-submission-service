package com.springcloud.agentportal.client;

import com.springcloud.agentportal.dto.NotificationRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for notifying Agent Portal about submission processing
 */
@FeignClient(name = "agentPortalNotifier", url = "${app.apis.agent-portal.base-url}")
public interface AgentPortalNotifierClient {

    /**
     * Sends notification to Agent Portal about submission status
     *
     * @param apiKey API key for authentication
     * @param userId User ID to notify
     * @param notification Notification details
     */
    @PostMapping("/notifyme/{userId}")
    void notifyUser(
        @RequestHeader("X-API-KEY") String apiKey,
        @PathVariable("userId") String userId,
        @RequestBody NotificationRequest notification
    );
}
