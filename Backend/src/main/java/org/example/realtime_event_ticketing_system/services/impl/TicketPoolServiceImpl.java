package org.example.realtime_event_ticketing_system.services.impl;

import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.example.realtime_event_ticketing_system.repositories.EventRepository;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.example.realtime_event_ticketing_system.utils.LockManager;



@Service
public class TicketPoolServiceImpl implements TicketPoolService {
    private final ConcurrentLinkedQueue<Ticket> ticketPool;
    private final ConcurrentHashMap<Long, Semaphore> eventSemaphores;
    private final ConcurrentHashMap<Long, ReentrantLock> eventLocks;
    private final ConcurrentHashMap<Long, AtomicInteger> eventAvailableTickets;
    private final ConcurrentHashMap<Long, AtomicInteger> eventSoldTickets;
    private final ConcurrentHashMap<Long, Integer> eventTotalTickets;
    private final ConcurrentHashMap<Long, Integer> eventMaxCapacities;
    private final ConcurrentHashMap<Long, Integer> eventTicketReleaseRates;
    private final ConcurrentHashMap<Long, Integer> eventCustomerRetrievalRates;

    LockManager lockManager = new LockManager();

    @Autowired
    private EventRepository eventRepository;

    public TicketPoolServiceImpl() {
        this.ticketPool = new ConcurrentLinkedQueue<>();
        this.eventSemaphores = new ConcurrentHashMap<>();
        this.eventLocks = new ConcurrentHashMap<>();
        this.eventAvailableTickets = new ConcurrentHashMap<>();
        this.eventSoldTickets = new ConcurrentHashMap<>();
        this.eventTotalTickets = new ConcurrentHashMap<>();
        this.eventMaxCapacities = new ConcurrentHashMap<>();
        this.eventTicketReleaseRates = new ConcurrentHashMap<>();
        this.eventCustomerRetrievalRates = new ConcurrentHashMap<>();
    }

    @Override
    public void configureEvent(Long eventId, TicketConfigDto config) {
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
        ReentrantLock lock = eventLocks.get(eventId);
        if (lock == null) {
            throw new ResourceNotFoundException("Event configuration not found");
        }

        try {
            lock.lock();
            AtomicInteger available = eventAvailableTickets.get(eventId);
            AtomicInteger sold = eventSoldTickets.get(eventId);
            Integer maxCapacity = eventMaxCapacities.get(eventId);
            Integer total = eventTotalTickets.get(eventId);

            if (available == null || sold == null || maxCapacity == null || total == null) {
                throw new TicketingException("Event not properly configured");
            }

            if (available.get() + sold.get() >= total) {
                throw new TicketingException("Total ticket limit reached");
            }

            if (available.get() >= maxCapacity) {
                throw new TicketingException("Maximum capacity reached");
            }

            ticketPool.offer(ticket);
            available.incrementAndGet();
            return true;
        } finally {
            lock.unlock();
        }
    }

//    @Override
//    public TicketConfigDto getEventStats(Long eventId) {
//        eventLocks.computeIfAbsent(eventId, k -> new ReentrantLock());
//        ReentrantLock lock = eventLocks.get(eventId);
//        if (lock == null) {
//            throw new ResourceNotFoundException("Event configuration not found");
//        }
//
//        try {
//            lock.lock();
//
//            AtomicInteger available = eventAvailableTickets.get(eventId);
//            AtomicInteger sold = eventSoldTickets.get(eventId);
//            Integer total = eventTotalTickets.get(eventId);
//            Integer maxCapacity = eventMaxCapacities.get(eventId);
//            Integer releaseRate = eventTicketReleaseRates.get(eventId);
//            Integer retrievalRate = eventCustomerRetrievalRates.get(eventId);
//
//            if (available == null || sold == null || total == null || maxCapacity == null ||
//                    releaseRate == null || retrievalRate == null) {
//                throw new TicketingException("Event not properly configured");
//            }
//
//            return TicketConfigDto.builder()
//                    .totalTickets(total)
//                    .maxTicketCapacity(maxCapacity)
//                    .ticketReleaseRate(releaseRate)
//                    .customerRetrievalRate(retrievalRate)
//                    .availableTickets(available.get())
//                    .soldTickets(sold.get())
//                    .build();
//        } finally {
//            lock.unlock();
//        }
//    }

    @Override
    public TicketConfigDto getEventStats(Long eventId) {
        ReentrantLock lock = lockManager.getLock(eventId);

        lock.lock(); // Lock for this event
        try {
            TicketConfig event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event configuration not found")).getTicketConfig();

            if (event.getAvailableTickets() == null || event.getSoldTickets() == null ||
                    event.getTotalTickets() == null || event.getMaxTicketCapacity() == null ||
                    event.getTicketReleaseRate() == null || event.getCustomerRetrievalRate() == null) {
                throw new TicketingException("Event not properly configured");
            }

            return TicketConfigDto.builder()
                    .totalTickets(event.getTotalTickets())
                    .maxTicketCapacity(event.getMaxTicketCapacity())
                    .ticketReleaseRate(event.getTicketReleaseRate())
                    .customerRetrievalRate(event.getCustomerRetrievalRate())
                    .availableTickets(event.getAvailableTickets())
                    .soldTickets(event.getSoldTickets())
                    .build();
        } finally {
            lock.unlock(); // Always unlock in the finally block
        }
    }


    @Override
    public void resetEvent(Long eventId) {
        ReentrantLock lock = eventLocks.get(eventId);
        if (lock != null) {
            try {
                lock.lock();
                eventSemaphores.remove(eventId);
                eventAvailableTickets.remove(eventId);
                eventSoldTickets.remove(eventId);
                eventTotalTickets.remove(eventId);
                eventMaxCapacities.remove(eventId);
                eventTicketReleaseRates.remove(eventId);
                eventCustomerRetrievalRates.remove(eventId);
                ticketPool.removeIf(ticket -> ticket.getEvent().getId().equals(eventId));
            } finally {
                lock.unlock();
                eventLocks.remove(eventId);
            }
        }
    }

    @Override
    public Ticket purchaseTicket(Long eventId, boolean isVipCustomer) throws InterruptedException {
        Semaphore semaphore = eventSemaphores.get(eventId);
        if (semaphore == null || !semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
            return null;
        }

        ReentrantLock lock = eventLocks.get(eventId);
        try {
            lock.lock();
            AtomicInteger available = eventAvailableTickets.get(eventId);
            AtomicInteger sold = eventSoldTickets.get(eventId);

            if (available.get() <= 0) {
                return null;
            }

            Ticket ticket = null;
            if (isVipCustomer) {
                ticket = ticketPool.stream()
                        .filter(t -> t.getEvent().getId().equals(eventId) && t.isVIP())
                        .findFirst()
                        .orElse(null);
            }

            if (ticket == null) {
                ticket = ticketPool.stream()
                        .filter(t -> t.getEvent().getId().equals(eventId))
                        .findFirst()
                        .orElse(null);
            }

            if (ticket != null) {
                ticketPool.remove(ticket);
                available.decrementAndGet();
                sold.incrementAndGet();
            }

            return ticket;
        } finally {
            lock.unlock();
            semaphore.release();
        }
    }
}

