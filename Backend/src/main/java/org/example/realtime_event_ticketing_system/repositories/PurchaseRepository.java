package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByTicketId(Long ticketId);
    @Modifying
    @Query("DELETE FROM Purchase p WHERE p.ticket.event.id = :eventId") // Delete all purchases for a specific event
    void deleteByEventId(@Param("eventId") Long eventId);
    @Modifying
    @Query("DELETE FROM Purchase p WHERE p.ticket.id = :ticketId") // Delete all purchases for a specific ticket
    void deleteByTicketId(@Param("ticketId") Long ticketId);

}
