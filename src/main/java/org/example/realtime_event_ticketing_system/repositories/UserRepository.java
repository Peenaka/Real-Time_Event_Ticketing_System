package org.example.realtime_event_ticketing_system.repositories;

import org.example.realtime_event_ticketing_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
