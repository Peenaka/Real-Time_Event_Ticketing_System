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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private final TicketConfigRepository configRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    @Lazy
    private final TicketPoolService ticketPoolService;

    @Override
    @Transactional
    public TicketConfig configureEvent(Long eventId, TicketConfigDto configDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (configRepository.existsByEventId(eventId)) {
            throw new TicketingException("Event is already configured");
        }

        TicketConfig config = TicketConfig.builder()
                .event(event)
                .totalTickets(configDto.getTotalTickets())
                .ticketReleaseRate(configDto.getTicketReleaseRate())
                .customerRetrievalRate(configDto.getCustomerRetrievalRate())
                .maxTicketCapacity(configDto.getMaxTicketCapacity())
                .isConfigured(true)
                .build();

        TicketConfig savedConfig = configRepository.save(config);
        ticketPoolService.configureEvent(eventId, configDto);
        return savedConfig;
    }

    @Override
    @Transactional
    public void resetEventConfig(Long eventId) {
        TicketConfig config = configRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found"));

        // Delete all tickets for the event
        ticketRepository.deleteByEventId(eventId);

        // Reset ticket pool
        ticketPoolService.resetEvent(eventId);

        // Delete configuration
        configRepository.delete(config);
    }

    @Override
    public TicketConfig getEventConfig(Long eventId) {
        return configRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found"));
    }
}