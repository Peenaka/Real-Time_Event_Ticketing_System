package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.models.Event;
import org.example.realtime_event_ticketing_system.models.TicketConfig;
import org.example.realtime_event_ticketing_system.models.EventStatus;
import org.example.realtime_event_ticketing_system.models.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    TicketConfig findById(long eventId);
    // Check if event code already exists
    boolean existsByEventCode(String eventCode);

//    @Query("SELECT e1_0.id,e1_0.event_code,e1_0.event_name,e1_0.status,tc1_0.is_configured from event e1_0 left join ticket_config tc1_0 on e1_0.id=tc1_0.event_id left join (vendor v1_0 join users v1_1 on v1_0.user_id=v1_1.id) on v1_0.user_id=e1_0.vendor_id where e1_0.id=?")
//    List<Event> findByEventId(@Param("name") String name);
}