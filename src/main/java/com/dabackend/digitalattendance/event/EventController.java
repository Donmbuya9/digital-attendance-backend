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

    // --- Admin Endpoints ---

    @PostMapping("/events")
    @PreAuthorize("hasRole('ADMINISTRATOR')") // UPDATED
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto requestDto) {
        return new ResponseEntity<>(eventService.createEvent(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMINISTRATOR')") // UPDATED
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')") // UPDATED
    public ResponseEntity<EventDetailDto> getEventDetails(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.getEventWithAttendance(eventId));
    }

    @PostMapping("/events/{eventId}/attendance/start")
    @PreAuthorize("hasRole('ADMINISTRATOR')") // UPDATED
    public ResponseEntity<StartAttendanceResponse> startAttendance(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.startAttendance(eventId));
    }

    // --- Attendee Endpoints ---

    @PostMapping("/events/{eventId}/attendance/mark")
    @PreAuthorize("hasRole('ATTENDEE')") // UPDATED
    public ResponseEntity<Void> markAttendance(
            @PathVariable UUID eventId,
            @RequestBody MarkAttendanceRequest request,
            Principal principal) {
        eventService.markAttendance(eventId, request, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/attendee/events")
    @PreAuthorize("hasRole('ATTENDEE')") // UPDATED
    public ResponseEntity<List<EventResponseDto>> getMyEvents(Principal principal) {
        return ResponseEntity.ok(eventService.getEventsForUser(principal));
    }
}