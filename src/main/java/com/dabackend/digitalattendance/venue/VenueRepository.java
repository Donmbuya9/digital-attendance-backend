package com.dabackend.digitalattendance.venue;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VenueRepository extends JpaRepository<Venue, UUID> {
    // This custom method will be useful later for checking for duplicate names.
    // Spring Data JPA automatically implements it based on the method name.
    Optional<Venue> findByName(String name);
}