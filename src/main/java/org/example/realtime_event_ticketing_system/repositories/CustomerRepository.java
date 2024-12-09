    package org.example.realtime_event_ticketing_system.repositories;

    import org.example.realtime_event_ticketing_system.models.Customer;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface CustomerRepository extends JpaRepository<Customer, Long> {

        Optional<Customer> findByEmail(String email);
        boolean existsByEmail(String email);
    }
