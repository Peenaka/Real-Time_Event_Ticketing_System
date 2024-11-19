package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Operation(summary = "Add new tickets by vendor")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tickets added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vendor not found")
    })
    @PostMapping("/vendor/{vendorId}/add")
    public ResponseEntity<ApiResponse<?>> createTickets(
            @RequestBody TicketDto ticketDto,
            @PathVariable Long vendorId) {
        try {
            Ticket ticket = ticketService.createTicket(ticketDto, vendorId);
            Map<String, Object> response = new HashMap<>();
            response.put("ticketId", ticket.getId());
            response.put("eventName", ticket.getEventName());
            response.put("price", ticket.getPrice());
            response.put("venue", ticket.getVenue());
            response.put("isVIP", ticket.isVIP());
            response.put("eventDateTime", ticket.getEventDateTime());

            return ResponseEntity.ok(ApiResponse.success("Tickets added successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Purchase a ticket")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket purchased successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Purchase failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found or No tickets available")
    })
    @PostMapping("/purchase/{customerId}")
    public ResponseEntity<ApiResponse<?>> purchaseTicket(
            @PathVariable Long customerId) {
        try {
            Ticket purchasedTicket = ticketService.purchaseTicket(customerId);
            Map<String, Object> response = new HashMap<>();
            response.put("ticketId", purchasedTicket.getId());
            response.put("eventName", purchasedTicket.getEventName());
            response.put("eventDateTime", purchasedTicket.getEventDateTime());
            response.put("venue", purchasedTicket.getVenue());

            return ResponseEntity.ok(ApiResponse.success("Ticket purchased successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Get available tickets count")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Available tickets count retrieved successfully")
    })
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<?>> getAvailableTickets() {
        try {
            int availableTickets = ticketService.getAvailableTickets();
            Map<String, Object> response = new HashMap<>();
            response.put("availableTickets", availableTickets);

            return ResponseEntity.ok(ApiResponse.success("Available tickets count retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Get ticket details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket details retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<?>> getTicketDetails(@PathVariable Long ticketId) {
        try {
            Ticket ticket = ticketService.getTicketDetails(ticketId);
            Map<String, Object> response = new HashMap<>();
            response.put("ticketId", ticket.getId());
            response.put("eventName", ticket.getEventName());
            response.put("price", ticket.getPrice());
            response.put("venue", ticket.getVenue());
            response.put("isVIP", ticket.isVIP());
            response.put("eventDateTime", ticket.getEventDateTime());
            response.put("isAvailable", ticket.isAvailable());

            return ResponseEntity.ok(ApiResponse.success("Ticket details retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

//    @PostMapping("/vendor/{vendorId}")
//    public ResponseEntity<ApiResponse<?>> createTicket(
//            @RequestBody TicketDto ticketDto,
//            @PathVariable Long vendorId) {
//        try {
//            Ticket ticket = ticketService.createTicket(ticketDto, vendorId);
//            return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", ticket));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    @GetMapping("/available")
//    public ResponseEntity<ApiResponse<?>> getAvailableTickets() {
//        try {
//            int availableTickets = ticketService.getAvailableTickets();
//            return ResponseEntity.ok(ApiResponse.success("Available tickets retrieved successfully", availableTickets));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }
}
