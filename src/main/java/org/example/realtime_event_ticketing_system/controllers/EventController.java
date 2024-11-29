package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.EventDto;
import org.example.realtime_event_ticketing_system.exceptions.AuthenticationException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private static final String CONFIG_VENDOR_EMAIL = "VendorUser@gmail.com";
    private static final String CONFIG_VENDOR_PASSWORD = "vendor@user123";

    private void validateConfigVendor(String email, String password) {
        if (!CONFIG_VENDOR_EMAIL.equals(email) || !CONFIG_VENDOR_PASSWORD.equals(password)) {
            throw new AuthenticationException("Unauthorized: Only config vendor can perform this action");
        }
    }

    @Operation(summary = "Create a new event")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid event data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createEvent(
            @Valid @RequestBody EventDto eventDto,
            @RequestParam String email,
            @RequestParam String password) {

        validateConfigVendor(email, password);
        Event event = eventService.createEvent(eventDto);
        return ResponseEntity.ok(ApiResponse.success("Event created successfully", event));
    }

    @Operation(summary = "Update an event")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid event data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<?>> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventDto eventDto,
            @RequestParam String email,
            @RequestParam String password) {

        validateConfigVendor(email, password);
        Event event = eventService.updateEvent(eventId, eventDto);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", event));
    }

    @Operation(summary = "Delete an event")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Event deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<?>> deleteEvent(
            @PathVariable Long eventId,
            @RequestParam String email,
            @RequestParam String password) {

        validateConfigVendor(email, password);
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
    }

    @Operation(summary = "Get all events")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    @Operation(summary = "Get event by ID")
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<?>> getEventById(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", event));
    }
}