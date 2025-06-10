package com.springcloud.agentportal.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for API key authentication
 * Checks for X-API-KEY header on protected endpoints
 */
@Component
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    
    @Value("${app.security.api-key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Skip filter for notification endpoints
        if (request.getRequestURI().startsWith("/notifyme")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip filter for actuator endpoints
        if (request.getRequestURI().startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip filter for OpenAPI docs
        if (request.getRequestURI().startsWith("/api-docs") || 
            request.getRequestURI().startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Check API key
        String apiKey = request.getHeader(API_KEY_HEADER);
        
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            log.warn("Unauthorized access attempt with invalid API key: {}", apiKey);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or missing API key");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
