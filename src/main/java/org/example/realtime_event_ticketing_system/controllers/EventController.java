package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.EventDto;
import org.example.realtime_event_ticketing_system.exceptions.AuthenticationException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.services.AuthService;
import org.example.realtime_event_ticketing_system.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final AuthService authService;

    @Operation(summary = "Create a new event")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createEvent(
            @Valid @RequestBody EventDto eventDto,
            @RequestHeader("X-Config-Email") String email,
            @RequestHeader("X-Config-Password") String password) {

        if (!authService.isConfigVendor(email, password)) {
            throw new AuthenticationException("Unauthorized: Only config vendor can create events");
        }

        Event event = eventService.createEvent(eventDto);
        return ResponseEntity.ok(ApiResponse.success("Event created successfully", event));
    }

    @Operation(summary = "Update an event")
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<?>> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventDto eventDto,
            @RequestHeader("X-Config-Email") String email,
            @RequestHeader("X-Config-Password") String password) {

        if (!authService.isConfigVendor(email, password)) {
            throw new AuthenticationException("Unauthorized: Only config vendor can update events");
        }

        Event event = eventService.updateEvent(eventId, eventDto);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", event));
    }

    @Operation(summary = "Delete an event")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<?>> deleteEvent(
            @PathVariable Long eventId,
            @RequestHeader("X-Config-Email") String email,
            @RequestHeader("X-Config-Password") String password) {

        if (!authService.isConfigVendor(email, password)) {
            throw new AuthenticationException("Unauthorized: Only config vendor can delete events");
        }

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