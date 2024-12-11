package org.example.realtime_event_ticketing_system.services;

import org.example.realtime_event_ticketing_system.dto.EventDto;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.models.Ticket;

import java.util.List;

public interface TicketService {

    Ticket createTicket(TicketDto ticketDto, Long vendorId, Long eventId);
    TicketConfigDto getTicketStats(Long eventId);
    Ticket purchaseTicket(Long customerId, Long id) throws InterruptedException;
    int getAvailableTickets(Long eventId);
    Ticket getTicketDetails(Long ticketId);
    void deleteTicketByCustomer(Long customerId, Long ticketId);
    void deleteTicketByVendor(Long vendorId, Long ticketId);
    List<Ticket> getAllTickets();
}
