package org.example.realtime_event_ticketing_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.realtime_event_ticketing_system.models.Ticket;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findFirstByEventIdAndIsAvailableTrue(Long eventId); // Find the first available ticket for an event

    @Modifying
    @Query("UPDATE Ticket t SET t.isAvailable = true WHERE t.event.id = :eventId")
    void resetEventTickets(Long eventId);  // Reset all tickets for an event

    void deleteByEventId(Long eventId); // Delete all tickets for an event
}
