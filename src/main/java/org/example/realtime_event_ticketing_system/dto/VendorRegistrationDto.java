    package org.example.realtime_event_ticketing_system.dto;
    import io.swagger.v3.oas.annotations.media.Schema;


    import lombok.Data;

    @Data
    public class VendorRegistrationDto {
        @Schema(description = "Name of the vendor", example = "Testing Vendor")
        private String name;
        @Schema(description = "Email address of the vendor", example = "TestingV@gmail.com")
        private String email;
        @Schema(description = "Password for the vendor account", example = "Testing@V.123")
        private String password;
        @Schema(description = "Company name associated with the vendor", example = "Testing Company")
        private String companyName;
    }
