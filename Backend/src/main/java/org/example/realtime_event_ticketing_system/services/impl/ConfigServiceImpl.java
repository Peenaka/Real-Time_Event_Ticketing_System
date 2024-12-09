package org.example.realtime_event_ticketing_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.repositories.EventRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketConfigRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketRepository;
import org.example.realtime_event_ticketing_system.services.ConfigService;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private final TicketConfigRepository configRepository;
    private final EventRepository eventRepository;
    private final TicketPoolService ticketPoolService;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public TicketConfig configureEvent(Long eventId, TicketConfigDto configDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (configRepository.existsByEventId(eventId)) {
            throw new TicketingException("Event is already configured");
        }

        validateConfiguration(configDto);

        TicketConfig config = TicketConfig.builder()
                .event(event)
                .totalTickets(configDto.getTotalTickets())
                .ticketReleaseRate(configDto.getTicketReleaseRate())
                .customerRetrievalRate(configDto.getCustomerRetrievalRate())
                .maxTicketCapacity(configDto.getMaxTicketCapacity())
                .isConfigured(true)
                .build();

        config = configRepository.save(config);

        ticketPoolService.configureEvent(eventId, configDto);

        return config;
    }

    @Override
    @Transactional
    public void resetEventConfig(Long eventId) {
        TicketConfig config = configRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found"));

        // Delete all tickets for this event
        ticketRepository.deleteByEventId(eventId);

        // Reset ticket pool
        ticketPoolService.resetEvent(eventId);

        // Delete the configuration
        configRepository.delete(config);

        // Clear the configuration from the event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        event.setTicketConfig(null);
        eventRepository.save(event);
    }

    @Override
    public TicketConfigDto getEventConfig(Long eventId) {
        TicketConfig ticketConfig = configRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found"));


        return new TicketConfigDto(
                ticketConfig.getTotalTickets(),
                ticketConfig.getTicketReleaseRate(),
                ticketConfig.getCustomerRetrievalRate(),
                ticketConfig.getMaxTicketCapacity(),
                ticketConfig.getEvent().getEventName(),
                ticketConfig.getEvent().getId());
    }

    private void validateConfiguration(TicketConfigDto configDto) {
        if (configDto.getMaxTicketCapacity() > configDto.getTotalTickets()) {
            throw new TicketingException("Max capacity cannot be greater than total tickets");
        }

        if (configDto.getTicketReleaseRate() > configDto.getMaxTicketCapacity()) {
            throw new TicketingException("Ticket release rate cannot be greater than max capacity");
        }
    }
}