package org.example.realtime_event_ticketing_system.dto;

import lombok.Data;

@Data
public class TicketConfigRequest {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;
}
