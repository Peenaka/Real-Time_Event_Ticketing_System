package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.config.TicketConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketConfigRepository extends JpaRepository<TicketConfig, Long> {
    boolean existsByIsConfigured(boolean isConfigured);
}
