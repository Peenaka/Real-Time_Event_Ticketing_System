package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.config.TicketConfig;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.repositories.EventRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {
    private final TicketConfigRepository configRepository;
    private final EventRepository eventRepository;

    @Operation(summary = "Configure tickets for an event")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event configured successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid configuration"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PostMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> configureEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody TicketConfigDto configDto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (configRepository.existsByEventId(eventId)) {
            throw new TicketingException("Event is already configured");
        }

        TicketConfig config = TicketConfig.builder()
                .event(event)
                .totalTickets(configDto.getTotalTickets())
                .ticketReleaseRate(configDto.getTicketReleaseRate())
                .customerRetrievalRate(configDto.getCustomerRetrievalRate())
                .maxTicketCapacity(configDto.getMaxTicketCapacity())
                .isConfigured(true)
                .build();

        config = configRepository.save(config);

        return ResponseEntity.ok(ApiResponse.success("Event configured successfully", config));
    }

    @Operation(summary = "Reset event configuration")
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> resetEventConfig(@PathVariable Long eventId) {
        TicketConfig config = configRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found"));

        configRepository.delete(config);
        return ResponseEntity.ok(ApiResponse.success("Event configuration reset successfully", null));
    }

    @Operation(summary = "Get event configuration")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> getEventConfig(@PathVariable Long eventId) {
        TicketConfig config = configRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found"));

        return ResponseEntity.ok(ApiResponse.success("Event configuration retrieved successfully", config));
    }
}