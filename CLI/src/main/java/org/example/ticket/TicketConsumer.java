package org.example.ticket;

import org.example.config.SystemConfig;

public class TicketConsumer implements Runnable {
    private final TicketPool ticketPool;
    private final SystemConfig config;

    public TicketConsumer(TicketPool ticketPool, SystemConfig config) {
        this.ticketPool = ticketPool;
        this.config = config;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                synchronized (config) {
                    if (config.getTotalTickets() <= 0 && ticketPool.getTicketCount() == 0) {
                        System.out.println("No tickets available to purchase. TicketConsumer stopping.");
                        break; // Exit when no tickets are left
                    }
                }

                int ticketsToRetrieve = config.getCustomerRetrievalRate();
                int ticketsPurchased = ticketPool.removeTickets(ticketsToRetrieve);
                System.out.println(ticketsPurchased + " tickets purchased by consumer.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("TicketConsumer interrupted.");
                break;
            }
        }
        System.out.println("TicketConsumer stopped.");
    }
}
