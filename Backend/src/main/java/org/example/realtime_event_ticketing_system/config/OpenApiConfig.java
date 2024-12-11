package org.example.realtime_event_ticketing_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customizes the OpenAPI (Swagger) documentation for the Real-Time Event Ticketing API.
 * This bean defines the metadata displayed in the API documentation, including title, version, description, contact information, and license details.
 */

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Real-Time Event Ticketing API")
                        .version("1.0")
                        .description("API documentation for the Real-Time Event Ticketing System")
                        .contact(new Contact()
                                .name("Event Ticketing Team")
                                .email("support@eventticketing.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}