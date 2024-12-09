package org.example.realtime_event_ticketing_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    void deleteByEventId(Long eventId);
    List<Ticket> findByEventId(Long eventId);
}
