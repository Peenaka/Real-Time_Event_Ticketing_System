package org.example.realtime_event_ticketing_system.services;

import org.example.realtime_event_ticketing_system.models.Ticket;

public interface TicketPoolService {
    void addTickets(Ticket ticket);

    Ticket purchaseTicket(boolean isVIPCustomer) throws InterruptedException;

    int getAvailableTickets();
}
