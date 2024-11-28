package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.config.TicketConfig;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.ConfigVendorLoginDto;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.exceptions.AuthenticationException;
import org.example.realtime_event_ticketing_system.services.AuthService;
import org.example.realtime_event_ticketing_system.services.ConfigService;
import org.example.realtime_event_ticketing_system.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {
    private final ConfigService configService;
    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(summary = "Config vendor login")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> configVendorLogin(@Valid @RequestBody ConfigVendorLoginDto loginDto) {
        if (!authService.isConfigVendor(loginDto.getEmail(), loginDto.getPassword())) {
            throw new AuthenticationException("Invalid config vendor credentials");
        }

        String token = jwtService.generateConfigVendorToken(loginDto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Login successful", token));
    }

    @Operation(summary = "Configure tickets for an event")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event configured successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid configuration"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PostMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> configureEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody TicketConfigDto configDto,
            @RequestHeader("Authorization") String token) {

        validateConfigVendorToken(token);
        TicketConfig config = configService.configureEvent(eventId, configDto);
        return ResponseEntity.ok(ApiResponse.success("Event configured successfully", config));
    }

    @Operation(summary = "Reset event configuration")
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> resetEventConfig(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String token) {

        validateConfigVendorToken(token);
        configService.resetEventConfig(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event configuration reset successfully", null));
    }

    @Operation(summary = "Get event configuration")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> getEventConfig(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String token) {

        validateConfigVendorToken(token);
        TicketConfig config = configService.getEventConfig(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event configuration retrieved successfully", config));
    }

    private void validateConfigVendorToken(String token) {
        if (!token.startsWith("Bearer ")) {
            throw new AuthenticationException("Invalid token format");
        }
        String jwt = token.substring(7);
        if (!jwtService.validateConfigVendorToken(jwt)) {
            throw new AuthenticationException("Invalid or expired token");
        }
    }
}