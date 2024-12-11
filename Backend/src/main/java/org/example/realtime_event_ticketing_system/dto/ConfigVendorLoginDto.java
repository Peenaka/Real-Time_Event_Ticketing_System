package org.example.realtime_event_ticketing_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfigVendorLoginDto {
    @Schema(description = "Config vendor email", example = "VendorUser@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Config vendor password", example = "vendor@user123")
    @NotBlank(message = "Password is required")
    private String password;
}