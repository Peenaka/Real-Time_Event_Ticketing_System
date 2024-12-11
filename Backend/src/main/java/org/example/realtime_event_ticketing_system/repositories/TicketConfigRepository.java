package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketConfigRepository extends JpaRepository<TicketConfig, Long> {
    Optional<TicketConfig> findByEventId(Long eventId); // Find event with pessimistic lock for updates
    boolean existsByEventId(Long eventId); // Check if event exists
}