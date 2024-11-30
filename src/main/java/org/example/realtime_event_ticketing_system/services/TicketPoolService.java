package org.example.realtime_event_ticketing_system.services;

import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.models.Ticket;

public interface TicketPoolService {
    void configureEvent(Long eventId, TicketConfigDto config);
    boolean addTickets(Long eventId, Ticket ticket) throws InterruptedException;
    Ticket purchaseTicket(Long eventId, boolean isVipCustomer) throws InterruptedException;
    TicketConfigDto getEventStats(Long eventId);
    void resetEvent(Long eventId);
}
