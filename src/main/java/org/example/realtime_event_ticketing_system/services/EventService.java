package org.example.realtime_event_ticketing_system.services;

import org.example.realtime_event_ticketing_system.dto.EventDto;
import org.example.realtime_event_ticketing_system.models.Event;

import java.util.List;

public interface EventService {
    Event createEvent(EventDto eventDto);
    Event updateEvent(Long eventId, EventDto eventDto);
    void deleteEvent(Long eventId);
    Event getEventById(Long eventId);
    List<Event> getAllEvents();
}