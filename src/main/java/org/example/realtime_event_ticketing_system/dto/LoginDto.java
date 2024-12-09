    package org.example.realtime_event_ticketing_system.dto;

    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.Data;

    @Data
    public class LoginDto {
        @Schema(description = "Email address of the User", example = "TestingUser@gmail.com")
        private String email;
        @Schema(description = "Password for the user account", example = "Testing@usr.123")
        private String password;
    }
