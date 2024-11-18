package org.example.realtime_event_ticketing_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Event Ticketing API", version = "1.0", description = "API Documentation"))
public class SwaggerConfig {
}