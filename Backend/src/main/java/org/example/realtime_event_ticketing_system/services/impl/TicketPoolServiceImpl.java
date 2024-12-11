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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class TicketPoolServiceImpl implements TicketPoolService {
    private final ConcurrentLinkedQueue<Ticket> ticketPool = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Long, Semaphore> eventSemaphores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ReentrantLock> eventLocks = new ConcurrentHashMap<>();

    private final EventRepository eventRepository;
    private final TicketConfigRepository ticketConfigRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void configureEvent(Long eventId, TicketConfigDto config) {
        ReentrantLock lock = eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock());

        try {
            lock.lock();
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

            TicketConfig ticketConfig = event.getTicketConfig();
            if (ticketConfig == null) {
                throw new TicketingException("Event configuration not found");
            }

            // Initialize semaphore for concurrent access control
            Semaphore semaphore = new Semaphore(config.getCustomerRetrievalRate());
            eventSemaphores.put(eventId, semaphore);

            // Update configuration in database
            ticketConfig.setTotalTickets(config.getTotalTickets());
            ticketConfig.setMaxTicketCapacity(config.getMaxTicketCapacity());
            ticketConfig.setTicketReleaseRate(config.getTicketReleaseRate());
            ticketConfig.setCustomerRetrievalRate(config.getCustomerRetrievalRate());
            ticketConfig.setAvailableTickets(0);
            ticketConfig.setSoldTickets(0);
            ticketConfig.setConfigured(true);

            ticketConfigRepository.save(ticketConfig);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public boolean addTickets(Long eventId, Ticket ticket) throws InterruptedException {
        ReentrantLock lock = eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock());

        try {
            lock.lock();
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

            config.setAvailableTickets(currentAvailable + 1);
            ticketConfigRepository.save(config);

            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Ticket purchaseTicket(Long eventId, boolean isVipCustomer) throws InterruptedException {
        Semaphore semaphore = eventSemaphores.computeIfAbsent(eventId, k -> new Semaphore(1));
        if (!semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
            return null;
        }

        ReentrantLock lock = eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock());
        try {
            lock.lock();
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

            // Update configuration
            config.setAvailableTickets(config.getAvailableTickets() - 1);
            config.setSoldTickets(config.getSoldTickets() + 1);
            ticketConfigRepository.save(config);

            return ticket;
        } finally {
            lock.unlock();
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
        if (lock != null) {
            try {
                lock.lock();
                eventSemaphores.remove(eventId);
                ticketPool.clear();

                TicketConfig config = getTicketConfig(eventId);
                config.setAvailableTickets(0);
                config.setSoldTickets(0);
                config.setConfigured(false);
                ticketConfigRepository.save(config);

                // Reset all tickets for this event
                ticketRepository.resetEventTickets(eventId);
            } finally {
                lock.unlock();
                eventLocks.remove(eventId);
            }
        }
    }

    private TicketConfig getTicketConfig(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        TicketConfig config = event.getTicketConfig();
        if (config == null || !config.isConfigured()) {
            throw new TicketingException("Event is not configured");
        }

        return config;
    }
}

