package org.example.realtime_event_ticketing_system.services;

import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;

public interface ConfigService {
    TicketConfig configureEvent(Long eventId, TicketConfigDto configDto);
    void resetEventConfig(Long eventId);
    TicketConfigDto getEventConfig(Long eventId);
}