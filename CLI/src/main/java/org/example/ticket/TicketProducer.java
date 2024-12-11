package org.example.ticket;

import org.example.config.SystemConfig;

public class TicketProducer implements Runnable {
    private final TicketPool ticketPool;
    private final SystemConfig config;

    public TicketProducer(TicketPool ticketPool, SystemConfig config) {
        this.ticketPool = ticketPool;
        this.config = config;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (config) {
                int remainingTickets = config.getTotalTickets();
                int poolSpace = config.getMaxTicketCapacity() - ticketPool.getTicketCount();

                if (remainingTickets <= 0) {
                    System.out.println("All tickets have been produced. TicketProducer stopping.");
                    break; // Stop producing tickets
                }

                if (poolSpace > 0) {
                    int ticketsToAdd = Math.min(Math.min(config.getTicketReleaseRate(), remainingTickets), poolSpace);
                    int ticketsAdded = ticketPool.addTickets(ticketsToAdd);
                    config.setTotalTickets(remainingTickets - ticketsAdded);
                    System.out.println(ticketsAdded + " tickets produced and added to the pool. Remaining tickets: " + config.getTotalTickets());
                } else {
                    System.out.println("Ticket pool is full. Waiting for space...");
                }
            }

            try {
                Thread.sleep(1000); // Simulate time taken for ticket production
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("TicketProducer interrupted.");
                break;
            }
        }
        System.out.println("TicketProducer stopped.");
    }
}
