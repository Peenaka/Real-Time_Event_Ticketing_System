package org.example.ticket;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketPool {
    private static final Logger logger = Logger.getLogger(TicketPool.class.getName());
    private final ConcurrentLinkedQueue<String> tickets = new ConcurrentLinkedQueue<>();
    private final int maxCapacity;
    private final AtomicInteger ticketIdGenerator = new AtomicInteger(1); // Unique ticket ID generator


    public TicketPool(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public synchronized int addTickets(int count) {
        int ticketsAdded = 0;
        while (ticketsAdded < count && tickets.size() < maxCapacity) {
            tickets.add("Ticket-" + (tickets.size() + 1));
            ticketsAdded++;
        }
        logger.log(Level.INFO, "Added {0} tickets. Total in pool: {1}", new Object[]{ticketsAdded, tickets.size()});
        return ticketsAdded;
    }

    public synchronized int removeTickets(int count) {
        int removedTickets = 0;
        while (removedTickets < count && !tickets.isEmpty()) {
            tickets.poll();
            removedTickets++;
        }

        if (removedTickets == 0) {
            logger.log(Level.INFO, "No tickets available to remove.");
        } else {
            logger.log(Level.INFO, "Tickets removed: {0}. Total tickets now: {1}", new Object[]{removedTickets, tickets.size()});
        }

        return removedTickets;
    }

    public synchronized int getTicketCount() {
        return tickets.size();
    }
}
