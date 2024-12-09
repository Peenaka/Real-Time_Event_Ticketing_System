    package org.example.realtime_event_ticketing_system.dto;


    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.Data;

    @Data
    public class CustomerRegistrationDto {
        @Schema(description = "Name of the Customer", example = "Testing Customer")
        private String name;
        @Schema(description = "Email address of the Customer", example = "TestingCustomer@gmail.com")
        private String email;
        @Schema(description = "Password for the customer account", example = "Testing@Cus.123")
        private String password;
        @Schema(description = "Indicates whether the customer is a VIP", example = "true")
        private boolean isVIP;
    }
