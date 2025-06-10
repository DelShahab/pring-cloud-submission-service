package com.windsurf.agentportal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "apiKey";
        
        return new OpenAPI()
            .info(new Info()
                .title("Agent Portal Submission Service API")
                .description("Service for handling ACORD file submissions via Origami and Roots.ai")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Windsurf Team")
                    .email("support@windsurf.com")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name("X-API-KEY")
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)));
    }
}
