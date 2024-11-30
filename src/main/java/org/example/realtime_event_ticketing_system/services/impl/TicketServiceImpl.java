package org.example.realtime_event_ticketing_system.services.impl;

import jakarta.transaction.Transactional;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.exceptions.ResourceNotFoundException;
import org.example.realtime_event_ticketing_system.exceptions.TicketingException;
import org.example.realtime_event_ticketing_system.models.Customer;
import org.example.realtime_event_ticketing_system.models.Purchase;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.models.Vendor;
import org.example.realtime_event_ticketing_system.repositories.CustomerRepository;
import org.example.realtime_event_ticketing_system.repositories.PurchaseRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketRepository;
import org.example.realtime_event_ticketing_system.repositories.VendorRepository;
import org.example.realtime_event_ticketing_system.services.TicketPoolService;
import org.example.realtime_event_ticketing_system.services.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    @Transactional
    public Ticket createTicket(TicketDto ticketDto, Long vendorId, Long eventId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Ticket ticket = new Ticket();
        ticket.setEventName(ticketDto.getEventName());
        ticket.setPrice(BigDecimal.valueOf(ticketDto.getPrice()));
        ticket.setEventDateTime(ticketDto.getEventDateTime());
        ticket.setVenue(ticketDto.getVenue());
        ticket.setVIP(ticketDto.isVIP());
        ticket.setVendor(vendor);
        ticket.setAvailable(true);

        ticket = ticketRepository.save(ticket);

        try {
            ticketPoolService.addTickets(eventId, ticket);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to add ticket to pool", e);
        }

        return ticket;
    }

    @Transactional
    @Override
    public Ticket purchaseTicket(Long customerId, Long eventId) throws InterruptedException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Ticket ticket = ticketPoolService.purchaseTicket(eventId, customer.isVIP());
        if (ticket == null) {
            throw new RuntimeException("No tickets available");
        }

        ticket.setAvailable(false);
        ticket = ticketRepository.save(ticket);

        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setTicket(ticket);
        purchaseRepository.save(purchase);

        return ticket;
    }

    @Override
    public int getAvailableTickets(Long eventId) {
        return ticketPoolService.getEventStats(eventId).getAvailableTickets();
    }

    @Override
    public Ticket getTicketDetails(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
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
            throw new TicketingException("Vendor is not authorized to delete this ticket");
        }

        if (ticket.isPurchased()) {
            throw new TicketingException("Cannot delete purchased ticket");
        }

        ticketRepository.delete(ticket);
    }
}