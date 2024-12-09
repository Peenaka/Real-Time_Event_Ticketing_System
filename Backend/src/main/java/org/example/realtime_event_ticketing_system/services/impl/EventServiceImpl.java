package org.example.realtime_event_ticketing_system.services.impl;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.EventDto;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.repositories.EventRepository;
import org.example.realtime_event_ticketing_system.services.EventService;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final TicketPoolService ticketPoolService;

    @Override
    @Transactional
    public Event createEvent(EventDto eventDto) {
        String eventCode = eventDto.getEventCode() != null ?
                eventDto.getEventCode() : generateEventCode();

        if (eventRepository.existsByEventCode(eventCode)) {
            throw new TicketingException("Event code already exists");
        }

        Event event = Event.builder()
                .eventName(eventDto.getEventName())
                .eventCode(eventCode)
                .status(eventDto.getStatus())
                .build();

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event updateEvent(Long eventId, EventDto eventDto) {
        Event event = eventRepository.getReferenceById(eventId);

        if (eventDto.getEventCode() != null &&
                !event.getEventCode().equals(eventDto.getEventCode()) &&
                eventRepository.existsByEventCode(eventDto.getEventCode())) {
            throw new TicketingException("Event code already exists");
        }

        event.setEventName(eventDto.getEventName());
        if (eventDto.getEventCode() != null) {
            event.setEventCode(eventDto.getEventCode());
        }
        event.setStatus(eventDto.getStatus());

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        resetEvent(eventId); // Reset ticket pool before deleting
        eventRepository.delete(event);
    }

    @Override
    public EventDto getEventById(Long eventId) {
        Event event = new Event();
        try {
            event = eventRepository.getReferenceById(eventId);
        } catch (Exception e ){
            e.printStackTrace();
        }
        return new EventDto(event.getId(),
                event.getEventName(),
                event.getEventCode(),
                event.getStatus(),
                event.getTicketConfig().isConfigured());
    }

    @Override
    public List<EventDto> getAllEvents() {
        List<EventDto> eventDtos = new ArrayList<>();
        List<Event> eventDto = new ArrayList<>();
        try {
             eventDto = eventRepository.findAll();
        } catch (Exception e ){
            e.printStackTrace();
        }
        for(Event event : eventDto){
            EventDto tempDto = new EventDto();
            tempDto.setEventName(event.getEventName());
            tempDto.setEventCode(event.getEventCode());
            tempDto.setId(event.getId());
            tempDto.setStatus(event.getStatus());
            if(event.getTicketConfig() != null){
                tempDto.setConfigured(event.getTicketConfig().isConfigured());
            }else{
                tempDto.setConfigured(false);
            }
            eventDtos.add(tempDto);

        }
        return eventDtos;
    }

    @Override
    @Transactional
    public void configureEvent(Long eventId, TicketConfigDto config) {
        // Verify event exists before configuring
        getEventById(eventId);
        ticketPoolService.configureEvent(eventId, config);
    }

    @Override
    @Transactional
    public void resetEvent(Long eventId) {
        // Verify event exists before resetting
        getEventById(eventId);
        ticketPoolService.resetEvent(eventId);
    }

    @Override
    public TicketConfigDto getEventConfiguration(Long eventId) {
        // Verify event exists before getting configuration
        getEventById(eventId);
        return ticketPoolService.getEventStats(eventId);
    }

    private String generateEventCode() {
        return "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}