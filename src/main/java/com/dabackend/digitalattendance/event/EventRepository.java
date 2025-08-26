package com.dabackend.digitalattendance.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Finds all events associated with a specific user by looking through their group memberships.
     * This is an efficient way to get an attendee's schedule.
     * JPQL (Java Persistence Query Language) is used to define the query.
     * @param userId The UUID of the user.
     * @return A list of events the user is scheduled to attend.
     */
    @Query("SELECT e FROM Event e JOIN e.group g JOIN g.members m WHERE m.user.id = :userId ORDER BY e.startTime DESC")
    List<Event> findEventsByUserId(UUID userId);
}