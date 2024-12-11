package org.example.realtime_event_ticketing_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.example.realtime_event_ticketing_system.repositories.EventRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketConfigRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketRepository;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*** Provides implementation for the TicketPoolService interface, managing ticket pools for events, including ticket addition, removal, and configuration, as well as event ticket sales and inventory management.*/

@Service
@RequiredArgsConstructor
public class TicketPoolServiceImpl implements TicketPoolService {
    private static final Logger logger = LoggerFactory.getLogger(TicketPoolServiceImpl.class);

    private final ConcurrentLinkedQueue<Ticket> ticketPool = new ConcurrentLinkedQueue<>(); // Queue to store available tickets
    private final ConcurrentHashMap<Long, Semaphore> eventSemaphores = new ConcurrentHashMap<>(); // Semaphore for concurrent access
    private final ConcurrentHashMap<Long, ReentrantLock> eventLocks = new ConcurrentHashMap<>(); // Lock for concurrent access

    private final EventRepository eventRepository;
    private final TicketConfigRepository ticketConfigRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void configureEvent(Long eventId, TicketConfigDto config) {
        ReentrantLock lock = eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock()); // Lock for concurrent access

        try {
            lock.lock();
            logger.info("Lock acquired for event ID: {}", eventId);
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

            TicketConfig ticketConfig = event.getTicketConfig();
            if (ticketConfig == null) {
                logger.error("Event configuration not found for event ID: {}", eventId);
                throw new TicketingException("Event configuration not found");
            }

            // Initialize semaphore for concurrent access control
            Semaphore semaphore = new Semaphore(config.getCustomerRetrievalRate());
            eventSemaphores.put(eventId, semaphore); // Add semaphore to map

            // Update configuration in database
            ticketConfig.setTotalTickets(config.getTotalTickets());
            ticketConfig.setMaxTicketCapacity(config.getMaxTicketCapacity());
            ticketConfig.setTicketReleaseRate(config.getTicketReleaseRate());
            ticketConfig.setCustomerRetrievalRate(config.getCustomerRetrievalRate());
            ticketConfig.setAvailableTickets(0);
            ticketConfig.setSoldTickets(0);
            ticketConfig.setConfigured(true);

            ticketConfigRepository.save(ticketConfig);
            logger.info("Event configuration updated for event ID: {}", eventId);
        } finally {
            lock.unlock();
            logger.info("Lock released for event ID: {}", eventId);
        }
    }

    @Override
    @Transactional
    public boolean addTickets(Long eventId, Ticket ticket) throws InterruptedException {
        ReentrantLock lock = eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock());

        try {
            lock.lock();
            logger.info("Lock acquired for event ID: {}", eventId);
            TicketConfig config = getTicketConfig(eventId);

            int currentAvailable = config.getAvailableTickets();
            if (currentAvailable >= config.getMaxTicketCapacity()) {
                return false;
            }

            if (currentAvailable + config.getSoldTickets() >= config.getTotalTickets()) {
                return false;
            }

            ticket.setAvailable(true);
            ticket.setPurchased(false);
            ticketRepository.save(ticket);
            logger.info("Ticket saved for event ID: {}", eventId);

            config.setAvailableTickets(currentAvailable + 1);
            ticketConfigRepository.save(config);
            logger.info("Ticket added for event ID: {}", eventId);

            return true;
        } finally {
            lock.unlock();
            logger.info("Lock released for event ID: {}", eventId);
        }
    }

    @Override
    @Transactional
    public Ticket purchaseTicket(Long eventId, boolean isVipCustomer) throws InterruptedException {
        Semaphore semaphore = eventSemaphores.computeIfAbsent(eventId, k -> new Semaphore(1)); // Semaphore for concurrent access
        if (!semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
            return null;
        }

        ReentrantLock lock = eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock());
        try {
            lock.lock();
            logger.info("Lock acquired for event ID: {}", eventId);
            TicketConfig config = getTicketConfig(eventId);

            if (config.getAvailableTickets() <= 0) {
                return null;
            }

            // Find an available ticket from the database
            Ticket ticket = ticketRepository.findFirstByEventIdAndIsAvailableTrue(eventId)
                    .orElse(null);

            if (ticket == null) {
                return null;
            }

            // Update ticket status
            ticket.setAvailable(false);
            ticket.setPurchased(true);
            ticketRepository.save(ticket);
            logger.info("Ticket purchased for event ID: {}", eventId);

            // Update configuration
            config.setAvailableTickets(config.getAvailableTickets() - 1);
            config.setSoldTickets(config.getSoldTickets() + 1);
            ticketConfigRepository.save(config);
            logger.info("Ticket configuration updated for event ID: {}", eventId);

            return ticket;
        } finally {
            lock.unlock();
            logger.info("Lock released for event ID: {}", eventId);
            semaphore.release();
        }
    }

    @Override
    public TicketConfigDto getEventStats(Long eventId) {
        TicketConfig config = getTicketConfig(eventId);

        return TicketConfigDto.builder()
                .totalTickets(config.getTotalTickets())
                .maxTicketCapacity(config.getMaxTicketCapacity())
                .ticketReleaseRate(config.getTicketReleaseRate())
                .customerRetrievalRate(config.getCustomerRetrievalRate())
                .availableTickets(config.getAvailableTickets())
                .soldTickets(config.getSoldTickets())
                .build();
    }

    @Override
    @Transactional
    public void resetEvent(Long eventId) {
        ReentrantLock lock = eventLocks.get(eventId);
        if (lock != null) { // Check if the lock exists
            try {
                lock.lock();
                eventSemaphores.remove(eventId);
                ticketPool.clear();

                TicketConfig config = getTicketConfig(eventId);
                config.setAvailableTickets(0);
                config.setSoldTickets(0);
                config.setConfigured(false);
                ticketConfigRepository.save(config);
                logger.info("Event configuration reset for event ID: {}", eventId);

                // Reset all tickets for this event
                ticketRepository.resetEventTickets(eventId);
                logger.info("Tickets reset for event ID: {}", eventId);
            } finally {
                lock.unlock();
                eventLocks.remove(eventId);
                logger.info("Lock released for event ID: {}", eventId);
            }
        }
    }

    private TicketConfig getTicketConfig(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        TicketConfig config = event.getTicketConfig();
        if (config == null || !config.isConfigured()) {
            logger.error("Event is not configured for event ID: {}", eventId);
            throw new TicketingException("Event is not configured");
        }

        return config;
    }
}

