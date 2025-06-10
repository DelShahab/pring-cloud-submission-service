package com.springcloud.agentportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Agent Portal Service
 * Handles submission flow with external API integrations
 */
@SpringBootApplication
@EnableFeignClients
public class AgentPortalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentPortalServiceApplication.class, args);
    }
}
