package org.example.realtime_event_ticketing_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.repositories.EventRepository;
import org.example.realtime_event_ticketing_system.repositories.PurchaseRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketConfigRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketRepository;
import org.example.realtime_event_ticketing_system.services.ConfigService;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*** Provides implementation for the ConfigService interface, handling configuration-related operations such as event configuration, ticket configuration, and system settings management.*/

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private final TicketConfigRepository configRepository;
    private final EventRepository eventRepository;
    private final TicketPoolService ticketPoolService;
    private final TicketRepository ticketRepository;
    private final PurchaseRepository purchaseRepository;

    @Override
    public TicketConfig configureEvent(Long eventId, TicketConfigDto configDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    logger.error("Event not found with ID {}", eventId);
                    return new ResourceNotFoundException("Event not found");
                });

        if (configRepository.existsByEventId(eventId)) {
            logger.error("Event is already configured with ID {}", eventId);
            throw new TicketingException("Event is already configured");
        }

        validateConfiguration(configDto); // Validate the configuration

          // Save the configuration
        TicketConfig config = TicketConfig.builder()
                .event(event)
                .totalTickets(configDto.getTotalTickets())
                .ticketReleaseRate(configDto.getTicketReleaseRate())
                .customerRetrievalRate(configDto.getCustomerRetrievalRate())
                .maxTicketCapacity(configDto.getMaxTicketCapacity())
                .availableTickets(configDto.getTotalTickets())
                .soldTickets(configDto.getSoldTickets())
                .isConfigured(true)
                .build();

        config = configRepository.save(config);
        logger.info("Event configured successfully with ID {}", eventId);
//        ticketPoolService.configureEvent(eventId, configDto);
        return config;
    }

    @Override
    @Transactional
    public void resetEventConfig(Long eventId) {
        TicketConfig config = configRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found"));
        purchaseRepository.deleteByEventId(eventId);

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
                .orElseThrow(() -> {
                    logger.error("Event configuration not found with ID {}", eventId);
                    return new ResourceNotFoundException("Event configuration not found");
                });

        // Convert TicketConfig to TicketConfigDto
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
            logger.error("Max capacity cannot be greater than total tickets");
            throw new TicketingException("Max capacity cannot be greater than total tickets");
        }

        if (configDto.getTicketReleaseRate() > configDto.getMaxTicketCapacity()) {
            logger.error("Ticket release rate cannot be greater than max capacity");
            throw new TicketingException("Ticket release rate cannot be greater than max capacity");
        }
    }
}