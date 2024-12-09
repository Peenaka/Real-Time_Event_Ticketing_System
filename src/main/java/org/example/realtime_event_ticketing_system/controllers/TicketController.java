package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.repositories.EventRepository;
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
    private final EventRepository eventRepository;

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
    @Operation(summary = "Delete purchased ticket by customer")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Delete failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @DeleteMapping("/customer/{customerId}/ticket/{ticketId}")
    public ResponseEntity<ApiResponse<?>> deleteTicketByCustomer(
            @PathVariable Long customerId,
            @PathVariable Long ticketId) {
        ticketService.deleteTicketByCustomer(customerId, ticketId);
        return ResponseEntity.ok(ApiResponse.success("Ticket deleted successfully", null));
    }

    @Operation(summary = "Delete ticket by vendor")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Delete failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @DeleteMapping("/vendor/{vendorId}/ticket/{ticketId}")
    public ResponseEntity<ApiResponse<?>> deleteTicketByVendor(
            @PathVariable Long vendorId,
            @PathVariable Long ticketId) {
        ticketService.deleteTicketByVendor(vendorId, ticketId);
        return ResponseEntity.ok(ApiResponse.success("Ticket deleted successfully", null));
    }
    @Operation(summary = "Get ticket statistics for an event")
    @GetMapping("/event/{eventId}/stats")
    public ResponseEntity<ApiResponse<?>> getTicketStats(@PathVariable Long eventId) {
        try {
            TicketConfigDto stats = ticketService.getTicketStats(eventId);
            Event event = eventRepository.getReferenceById(eventId);
            Map<String, Object> response = new HashMap<>();
            response.put("eventId", eventId);
            response.put("eventName", event.getEventName());
            response.put("totalTickets", stats.getTotalTickets());
            response.put("availableTickets", stats.getAvailableTickets());
            response.put("soldTickets", stats.getSoldTickets());
            response.put("maxCapacity", stats.getMaxTicketCapacity());

            return ResponseEntity.ok(ApiResponse.success("Ticket statistics retrieved successfully", response));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TicketingException(e.getMessage());
        }
    }
}