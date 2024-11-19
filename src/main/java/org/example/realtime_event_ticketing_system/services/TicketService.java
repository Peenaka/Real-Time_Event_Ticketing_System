package org.example.realtime_event_ticketing_system.services;

import jakarta.transaction.Transactional;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.models.Ticket;

public interface TicketService {

    Ticket createTicket(TicketDto ticketDto, Long vendorId);

    Ticket purchaseTicket(Long customerId) throws InterruptedException;

    int getAvailableTickets();

    Ticket getTicketDetails(Long ticketId);
}
