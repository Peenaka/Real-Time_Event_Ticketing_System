package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByTicketId(Long ticketId);
}
