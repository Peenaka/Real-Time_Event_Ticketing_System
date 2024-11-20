package org.example.realtime_event_ticketing_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByPurchased(boolean purchased);
}
