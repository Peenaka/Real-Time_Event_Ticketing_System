    package org.example.realtime_event_ticketing_system.repositories;

    import org.example.realtime_event_ticketing_system.models.Vendor;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface VendorRepository extends JpaRepository<Vendor, Long> {
        Optional<Vendor> findByEmail(String email); // Find vendor by email
        boolean existsByEmail(String email); // Check if vendor email exists
    }
