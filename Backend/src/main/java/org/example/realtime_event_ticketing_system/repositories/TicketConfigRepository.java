package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TicketConfigRepository extends JpaRepository<TicketConfig, Long> {
    Optional<TicketConfig> findByEventId(Long eventId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT tc FROM TicketConfig tc WHERE tc.event.id = :eventId")
    Optional<TicketConfig> findByEventIdWithLock(@Param("eventId") Long eventId);

    boolean existsByEventId(Long eventId);
}