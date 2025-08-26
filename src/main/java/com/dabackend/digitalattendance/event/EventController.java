package com.dabackend.digitalattendance.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/events")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto requestDto) {
        return new ResponseEntity<>(eventService.createEvent(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/events")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<EventDetailDto> getEventDetails(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.getEventWithAttendance(eventId));
    }

    @PostMapping("/events/{eventId}/attendance/start")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<StartAttendanceResponse> startAttendance(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.startAttendance(eventId));
    }

    @PostMapping("/events/{eventId}/attendance/mark")
    @PreAuthorize("hasAuthority('ATTENDEE')")
    public ResponseEntity<Void> markAttendance(
            @PathVariable UUID eventId,
            @RequestBody MarkAttendanceRequest request,
            Principal principal) {
        eventService.markAttendance(eventId, request, principal);
        return ResponseEntity.ok().build();
    }

    // This is the new endpoint for attendees to view their own events
    @GetMapping("/attendee/events")
    @PreAuthorize("hasAuthority('ATTENDEE')")
    public ResponseEntity<List<EventResponseDto>> getMyEvents(Principal principal) {
        // This is a simplified implementation. A real app might have a dedicated service method.
        // For now, we can filter all events. This is NOT efficient for large scale apps.
        // A better approach would be custom queries.
        return ResponseEntity.ok(List.of()); // Placeholder for now
    }
}