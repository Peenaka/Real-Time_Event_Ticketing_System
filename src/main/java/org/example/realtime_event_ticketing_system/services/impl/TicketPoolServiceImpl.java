package org.example.realtime_event_ticketing_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.*;
import org.example.realtime_event_ticketing_system.repositories.*;
import org.example.realtime_event_ticketing_system.services.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class TicketPoolServiceImpl implements TicketPoolService {
    private final ConcurrentLinkedQueue<Ticket> ticketPool = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Long, Semaphore> eventSemaphores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ReentrantLock> eventLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> eventAvailableTickets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> eventSoldTickets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> eventTotalTickets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> eventMaxCapacities = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> eventTicketReleaseRates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> eventCustomerRetrievalRates = new ConcurrentHashMap<>();

    private final VendorRepository vendorRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final ConfigService configService;

    @Override
    public void configureEvent(Long eventId, TicketConfigDto config) {
        validateEventExists(eventId);

        eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock());
        ReentrantLock lock = eventLocks.get(eventId);

        try {
            lock.lock();
            eventTotalTickets.put(eventId, config.getTotalTickets());
            eventMaxCapacities.put(eventId, config.getMaxTicketCapacity());
            eventTicketReleaseRates.put(eventId, config.getTicketReleaseRate());
            eventCustomerRetrievalRates.put(eventId, config.getCustomerRetrievalRate());

            eventAvailableTickets.putIfAbsent(eventId, new AtomicInteger(0));
            eventSoldTickets.putIfAbsent(eventId, new AtomicInteger(0));

            Semaphore semaphore = new Semaphore(config.getCustomerRetrievalRate());
            eventSemaphores.put(eventId, semaphore);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addTickets(Long eventId, Ticket ticket) throws InterruptedException {
        validateEventExists(eventId);
        ReentrantLock lock = eventLocks.get(eventId);
        if (lock == null) return false;

        try {
            lock.lock();
            AtomicInteger available = eventAvailableTickets.get(eventId);
            AtomicInteger sold = eventSoldTickets.get(eventId);
            Integer maxCapacity = eventMaxCapacities.get(eventId);
            Integer total = eventTotalTickets.get(eventId);

            if (available.get() >= maxCapacity ||
                    sold.get() + available.get() >= total) {
                return false;
            }

            ticketPool.offer(ticket);
            available.incrementAndGet();
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Ticket addTickets(TicketDto ticketDto, Long vendorId, Long eventId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        TicketConfig config = configService.getEventConfig(eventId);

        validateTicketCreation(eventId, config);

        Ticket ticket = createTicket(ticketDto, vendor, event);
        Ticket savedTicket = ticketRepository.save(ticket);

        try {
            if (!addTickets(eventId, savedTicket)) {
                throw new TicketingException("Failed to add ticket to pool - capacity exceeded");
            }
        } catch (InterruptedException e) {
            throw new TicketingException("Failed to add ticket to pool");
        }

        return savedTicket;
    }

    @Override
    public Ticket purchaseTicket(Long eventId, boolean isVipCustomer) throws InterruptedException {
        validateEventExists(eventId);
        Semaphore semaphore = eventSemaphores.get(eventId);
        if (semaphore == null || !semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
            throw new TicketingException("Unable to process ticket purchase at this time");
        }

        ReentrantLock lock = eventLocks.get(eventId);
        try {
            lock.lock();
            return processTicketPurchase(eventId, isVipCustomer);
        } finally {
            lock.unlock();
            semaphore.release();
        }
    }

    @Override
    public TicketConfigDto getEventStats(Long eventId) {
        validateEventExists(eventId);
        AtomicInteger available = eventAvailableTickets.get(eventId);
        AtomicInteger sold = eventSoldTickets.get(eventId);

        return TicketConfigDto.builder()
                .totalTickets(eventTotalTickets.get(eventId))
                .maxTicketCapacity(eventMaxCapacities.get(eventId))
                .ticketReleaseRate(eventTicketReleaseRates.get(eventId))
                .customerRetrievalRate(eventCustomerRetrievalRates.get(eventId))
                .availableTickets(available != null ? available.get() : 0)
                .soldTickets(sold != null ? sold.get() : 0)
                .build();
    }

    @Override
    public void resetEvent(Long eventId) {
        validateEventExists(eventId);
        ReentrantLock lock = eventLocks.get(eventId);
        if (lock != null) {
            try {
                lock.lock();
                clearEventData(eventId);
            } finally {
                lock.unlock();
                eventLocks.remove(eventId);
            }
        }
    }

    private void validateEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }
    }

    private void validateTicketCreation(Long eventId, TicketConfig config) {
        int currentTickets = (int) ticketRepository.countByEventId(eventId);
        if (currentTickets + 1 > config.getTotalTickets()) {
            throw new TicketingException("Cannot exceed total ticket limit configured for the event");
        }
    }

    private Ticket createTicket(TicketDto ticketDto, Vendor vendor, Event event) {
        Ticket ticket = new Ticket();
        ticket.setEventName(ticketDto.getEventName());
        ticket.setPrice(ticketDto.getPrice());
        ticket.setVIP(ticketDto.isVIP());
        ticket.setVendor(vendor);
        ticket.setEvent(event);
        ticket.setAvailable(true);
        return ticket;
    }

    private Ticket processTicketPurchase(Long eventId, boolean isVipCustomer) {
        AtomicInteger available = eventAvailableTickets.get(eventId);
        AtomicInteger sold = eventSoldTickets.get(eventId);

        if (available.get() <= 0) {
            return null;
        }

        Ticket ticket = findAvailableTicket(eventId, isVipCustomer);
        if (ticket != null) {
            ticketPool.remove(ticket);
            available.decrementAndGet();
            sold.incrementAndGet();
        }

        return ticket;
    }

    private Ticket findAvailableTicket(Long eventId, boolean isVipCustomer) {
        if (isVipCustomer) {
            return ticketPool.stream()
                    .filter(t -> t.getEvent().getId().equals(eventId) && t.isVIP())
                    .findFirst()
                    .orElseGet(() -> findRegularTicket(eventId));
        }
        return findRegularTicket(eventId);
    }

    private Ticket findRegularTicket(Long eventId) {
        return ticketPool.stream()
                .filter(t -> t.getEvent().getId().equals(eventId))
                .findFirst()
                .orElse(null);
    }

    private void clearEventData(Long eventId) {
        eventSemaphores.remove(eventId);
        eventAvailableTickets.remove(eventId);
        eventSoldTickets.remove(eventId);
        eventTotalTickets.remove(eventId);
        eventMaxCapacities.remove(eventId);
        eventTicketReleaseRates.remove(eventId);
        eventCustomerRetrievalRates.remove(eventId);
        ticketPool.removeIf(ticket -> ticket.getEvent().getId().equals(eventId));
    }
}