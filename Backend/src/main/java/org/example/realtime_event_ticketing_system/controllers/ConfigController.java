package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.services.ConfigService;
import org.example.realtime_event_ticketing_system.utils.AuthenticationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {
    private final ConfigService configService;
    private final AuthenticationUtils authenticationUtils;

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
            @RequestParam String email,
            @RequestParam String password) {

        authenticationUtils.validateConfigVendor(email, password);
        TicketConfig config = configService.configureEvent(eventId, configDto);
        return ResponseEntity.ok(ApiResponse.success("Event configured successfully", config));
    }

    @Operation(summary = "Reset event configuration")
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> resetEventConfig(
            @PathVariable Long eventId,
            @RequestParam String email,
            @RequestParam String password) {

        authenticationUtils.validateConfigVendor(email, password);
        configService.resetEventConfig(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event configuration reset successfully", null));
    }

    @Operation(summary = "Get event configuration")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> getEventConfig(
            @PathVariable Long eventId,
            @RequestParam String email,
            @RequestParam String password) {

        authenticationUtils.validateConfigVendor(email, password);
        TicketConfigDto config = configService.getEventConfig(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event configuration retrieved successfully", config));
    }
}