package org.example.realtime_event_ticketing_system.services;

import org.example.realtime_event_ticketing_system.dto.EventDto;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.models.Event;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventService {
    Event createEvent(EventDto eventDto);
    Event updateEvent(Long eventId, EventDto eventDto);
    void deleteEvent(Long eventId);
    EventDto getEventById(Long eventId);
    List<EventDto> getAllEvents();
    void configureEvent(Long eventId, TicketConfigDto config);
    void resetEvent(Long eventId);
    TicketConfigDto getEventConfiguration(Long eventId);
}