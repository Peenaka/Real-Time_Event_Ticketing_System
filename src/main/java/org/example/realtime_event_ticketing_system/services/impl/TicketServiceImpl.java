package org.example.realtime_event_ticketing_system.services.impl;

import jakarta.transaction.Transactional;
import org.example.realtime_event_ticketing_system.dto.TicketDto;
import org.example.realtime_event_ticketing_system.models.Customer;
import org.example.realtime_event_ticketing_system.models.Purchase;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.example.realtime_event_ticketing_system.models.Vendor;
import org.example.realtime_event_ticketing_system.repositories.CustomerRepository;
import org.example.realtime_event_ticketing_system.repositories.PurchaseRepository;
import org.example.realtime_event_ticketing_system.repositories.TicketRepository;
import org.example.realtime_event_ticketing_system.repositories.VendorRepository;
import org.example.realtime_event_ticketing_system.services.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private TicketPoolServiceImpl ticketPoolService;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    @Transactional
    public Ticket createTicket(TicketDto ticketDto, Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Ticket ticket = new Ticket();
        ticket.setEventName(ticketDto.getEventName());
        ticket.setPrice(ticketDto.getPrice());
        ticket.setEventDateTime(ticketDto.getEventDateTime());
        ticket.setVenue(ticketDto.getVenue());
        ticket.setVIP(ticketDto.isVIP());
        ticket.setVendor(vendor);
        ticket.setAvailable(true);

        ticket = ticketRepository.save(ticket);
        ticketPoolService.addTickets(ticket);

        return ticket;
    }

    @Transactional
    @Override
    public Ticket purchaseTicket(Long customerId) throws InterruptedException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Ticket ticket = ticketPoolService.purchaseTicket(customer.isVIP());
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
    public int getAvailableTickets() {
        return ticketPoolService.getAvailableTickets();
    }

    @Override
    public Ticket getTicketDetails(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }
}
