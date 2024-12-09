package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.dto.EventDto;
import org.example.realtime_event_ticketing_system.models.Event;
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

    // Find event by event code
    Optional<Event> findByEventCode(String eventCode);

    // Find events by vendor
    List<Event> findByVendor(Vendor vendor);

    // Find events by status
    List<Event> findByStatus(EventStatus status);

    // Find events by vendor and status
    List<Event> findByVendorAndStatus(Vendor vendor, EventStatus status);

    // Check if event code exists
    boolean existsByEventCode(String eventCode);

    // Find active events by vendor
    @Query("SELECT e FROM Event e WHERE e.vendor = :vendor AND e.status = 'ACTIVE'")
    List<Event> findActiveEventsByVendor(@Param("vendor") Vendor vendor);

    // Find event with pessimistic lock for updates
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithLock(@Param("id") Long id);

    // Count events by vendor and status
    long countByVendorAndStatus(Vendor vendor, EventStatus status);

    // Find events by name containing (case-insensitive)
    @Query("SELECT e FROM Event e WHERE LOWER(e.eventName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Event> findByEventNameContainingIgnoreCase(@Param("name") String name);

    // Find upcoming events (status = SCHEDULED)
    @Query("SELECT e FROM Event e WHERE e.status = 'SCHEDULED' ORDER BY e.createdAt ASC")
    List<Event> findUpcomingEvents();

    // Find events by vendor with pagination
    @Query("SELECT e FROM Event e WHERE e.vendor = :vendor ORDER BY e.createdAt DESC")
    List<Event> findByVendorWithPagination(@Param("vendor") Vendor vendor, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT e.id AS id, e.eventCode AS eventCode, e.eventName AS eventName, e.status AS status FROM Event e")
    List<Event> findEventBasicDetails();
}