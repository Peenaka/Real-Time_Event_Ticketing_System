package org.example.realtime_event_ticketing_system.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.*;
import org.example.realtime_event_ticketing_system.repositories.*;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.example.realtime_event_ticketing_system.services.TicketService;
import org.example.realtime_event_ticketing_system.utils.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/*** Provides implementation for the TicketService interface, handling ticket-related operations such as ticket creation, retrieval, updating, and deletion, as well as ticket sales, inventory management, and event ticket association.*/

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final TicketPoolService ticketPoolService;
    private final PurchaseRepository purchaseRepository;
    private final EventRepository eventRepository;

    LockManager lockManager = new LockManager();

    @Override
    @Transactional
    public Ticket createTicket(TicketDto ticketDto, Long vendorId, Long eventId) {
        ReentrantLock lock = lockManager.getLock(eventId);

        lock.lock(); // Lock for this event
        logger.info("Lock for event {} locked", eventId);

        try {
            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

            TicketConfigDto eventStats = ticketPoolService.getEventStats(eventId);

            if (!event.getTicketConfig().isConfigured()) {
                logger.error("Event not configured properly");
                throw new TicketingException("Event not configured properly");
            }

            int currentTotal = eventStats.getSoldTickets() + eventStats.getAvailableTickets();
            if (currentTotal + ticketDto.getTicketCount() > eventStats.getTotalTickets()) {
                logger.error("Cannot add tickets. Would exceed total ticket limit.");
                throw new TicketingException("Cannot add tickets. Would exceed total ticket limit.");
            }

            List<Ticket> tickets = new ArrayList<>();
            for (int i = 0; i < ticketDto.getTicketCount(); i++) {
                Ticket ticket = new Ticket();
                ticket.setEventName(ticketDto.getEventName());
                ticket.setPrice(BigDecimal.valueOf(ticketDto.getPrice()));
                ticket.setEventDateTime(ticketDto.getEventDateTime());
                ticket.setVenue(ticketDto.getVenue());
                ticket.setVIP(ticketDto.isVIP());
                ticket.setVendor(vendor);
                ticket.setEvent(event);
                ticket.setAvailable(true);

                tickets.add(ticketRepository.save(ticket));
                logger.info("Ticket created: {}", ticket);

                try {
                    boolean added = ticketPoolService.addTickets(eventId, ticket);
                    if (!added) {
                        logger.error("Failed to add ticket to pool");
                        throw new TicketingException("Failed to add ticket to pool");
                    }
                } catch (InterruptedException e) {
                    logger.error("Failed to add ticket to pool", e);
                    throw new TicketingException("Failed to add ticket to pool");
                }
            }

            return tickets.get(0); // Return first ticket as reference
        } finally {
            lock.unlock(); // Always unlock in the finally block
           logger.info("Lock for event {} unlocked", eventId);
        }
    }

    @Override
    public TicketConfigDto getTicketStats(Long eventId) {
        return ticketPoolService.getEventStats(eventId);
    }


    @Transactional
    @Override
    public Ticket purchaseTicket(Long customerId, Long eventId) throws InterruptedException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        TicketConfigDto eventStats = ticketPoolService.getEventStats(eventId);

        Ticket ticket = ticketPoolService.purchaseTicket(eventId, customer.isVIP());
        if (ticket == null) {
            logger.error("No tickets available");
            throw new TicketingException("No tickets available");
        }

        ticket.setAvailable(false);
        ticket.setPurchased(true);
        ticket = ticketRepository.save(ticket);


        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setTicket(ticket);
        purchaseRepository.save(purchase);
        logger.info("Ticket purchased: {}", ticket);

        return ticket;
    }

    @Override
    public int getAvailableTickets(Long eventId) {
        return ticketPoolService.getEventStats(eventId).getAvailableTickets();
    }

    @Override
    public Ticket getTicketDetails(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    @Override
    @Transactional
    public void deleteTicketByCustomer(Long customerId, Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        Purchase purchase = purchaseRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found for ticket"));

        if (!purchase.getCustomer().getId().equals(customerId)) {
            throw new TicketingException("Customer is not authorized to delete this ticket");
        }

        if (!ticket.isPurchased()) {
            logger.error("Cannot delete unpurchased ticket");
            throw new TicketingException("Cannot delete unpurchased ticket");
        }

        purchaseRepository.delete(purchase);
        ticketRepository.delete(ticket);
    }

    @Override
    @Transactional
    public void deleteTicketByVendor(Long vendorId, Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (!ticket.getVendor().getId().equals(vendorId)) {
            logger.error("Vendor is not authorized to delete this ticket");
            throw new TicketingException("Vendor is not authorized to delete this ticket");
        }

        if (ticket.isPurchased()) {
            logger.error("Cannot delete purchased ticket");
            throw new TicketingException("Cannot delete purchased ticket");
        }

        ticketRepository.delete(ticket);
    }
    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        purchaseRepository.deleteByTicketId(ticketId); // Delete purchases first
        ticketRepository.deleteById(ticketId);        // Then delete the ticket
    }
}