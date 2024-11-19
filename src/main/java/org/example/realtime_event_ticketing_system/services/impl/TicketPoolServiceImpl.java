package org.example.realtime_event_ticketing_system.services.impl;

import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TicketPoolServiceImpl implements TicketPoolService {
    private final ConcurrentLinkedQueue<Ticket> ticketPool;
    private final Semaphore ticketSemaphore;
    private final ReentrantLock lock;
    private final int MAX_TICKETS = 1000;

    public TicketPoolServiceImpl() {
        this.ticketPool = new ConcurrentLinkedQueue<>();
        this.ticketSemaphore = new Semaphore(MAX_TICKETS);
        this.lock = new ReentrantLock();
    }
@Override
public void addTickets(Ticket ticket) {
        try {
            lock.lock();
            if (ticketPool.size() < MAX_TICKETS) {
                ticketPool.offer(ticket);
                ticketSemaphore.release();
            }
        } finally {
            lock.unlock();
        }
    }
@Override
public Ticket purchaseTicket(boolean isVIPCustomer) throws InterruptedException {
        ticketSemaphore.acquire();
        try {
            lock.lock();
            if (isVIPCustomer) {
                // VIP customers get priority access to VIP tickets
                Ticket vipTicket = ticketPool.stream()
                        .filter(Ticket::isVIP)
                        .findFirst()
                        .orElse(null);

                if (vipTicket != null) {
                    ticketPool.remove(vipTicket);
                    return vipTicket;
                }
            }
            return ticketPool.poll();
        } finally {
            lock.unlock();
        }
    }
@Override
public int getAvailableTickets() {
        return ticketPool.size();
    }
}
