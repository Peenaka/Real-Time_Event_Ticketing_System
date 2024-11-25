package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.services.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @Operation(summary = "Add new tickets by vendor")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tickets added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vendor not found")
    })
    @PostMapping("/vendor/{vendorId}/event/{eventId}")
    public ResponseEntity<ApiResponse<?>> createTickets(
            @PathVariable Long vendorId,
            @PathVariable Long eventId,
            @Valid @RequestBody TicketDto ticketDto) {
        try {
            Ticket ticket = ticketService.createTicket(ticketDto, vendorId, eventId);
            Map<String, Object> response = new HashMap<>();
            response.put("ticketId", ticket.getId());
            response.put("eventName", ticket.getEventName());
            response.put("price", ticket.getPrice());
            response.put("isVIP", ticket.isVIP());
            response.put("eventDateTime", ticket.getCreatedAt());

            return ResponseEntity.ok(ApiResponse.success("Tickets added successfully", response));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TicketingException(e.getMessage());
        }
    }

    @Operation(summary = "Purchase a ticket")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket purchased successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Purchase failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer or Event not found")
    })
    @PostMapping("/event/{eventId}/purchase/{customerId}")
    public ResponseEntity<ApiResponse<?>> purchaseTicket(
            @PathVariable Long eventId,
            @PathVariable Long customerId) {
        try {
            Ticket purchasedTicket = ticketService.purchaseTicket(eventId, customerId);
            Map<String, Object> response = new HashMap<>();
            response.put("ticketId", purchasedTicket.getId());
            response.put("eventName", purchasedTicket.getEventName());
            response.put("eventDateTime", purchasedTicket.getCreatedAt());
            response.put("isVIP", purchasedTicket.isVIP());

            return ResponseEntity.ok(ApiResponse.success("Ticket purchased successfully", response));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TicketingException(e.getMessage());
        }
    }

    @Operation(summary = "Get available tickets count for an event")
    @GetMapping("/event/{eventId}/available")
    public ResponseEntity<ApiResponse<?>> getAvailableTickets(@PathVariable Long eventId) {
        try {
            int availableTickets = ticketService.getAvailableTickets(eventId);
            Map<String, Object> response = new HashMap<>();
            response.put("eventId", eventId);
            response.put("availableTickets", availableTickets);

            return ResponseEntity.ok(ApiResponse.success("Available tickets count retrieved successfully", response));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TicketingException(e.getMessage());
        }
    }

    @Operation(summary = "Get ticket details")
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<?>> getTicketDetails(@PathVariable Long ticketId) {
        try {
            Ticket ticket = ticketService.getTicketDetails(ticketId);
            Map<String, Object> response = new HashMap<>();
            response.put("ticketId", ticket.getId());
            response.put("eventName", ticket.getEventName());
            response.put("price", ticket.getPrice());
            response.put("isVIP", ticket.isVIP());
            response.put("eventDateTime", ticket.getCreatedAt());
            response.put("isAvailable", ticket.isAvailable());

            return ResponseEntity.ok(ApiResponse.success("Ticket details retrieved successfully", response));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TicketingException(e.getMessage());
        }
    }
}