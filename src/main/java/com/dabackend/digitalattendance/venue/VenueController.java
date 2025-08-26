package com.dabackend.digitalattendance.venue;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
// UPDATED: Changed to the more conventional 'hasRole' check.
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<VenueDto> createVenue(@RequestBody VenueDto venueDto) {
        VenueDto createdVenue = venueService.createVenue(venueDto);
        return new ResponseEntity<>(createdVenue, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VenueDto>> getAllVenues() {
        List<VenueDto> venues = venueService.getAllVenues();
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<VenueDto> getVenueById(@PathVariable UUID venueId) {
        return ResponseEntity.ok(venueService.getVenueById(venueId));
    }

    @PutMapping("/{venueId}")
    public ResponseEntity<VenueDto> updateVenue(@PathVariable UUID venueId, @RequestBody VenueDto venueDto) {
        return ResponseEntity.ok(venueService.updateVenue(venueId, venueDto));
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteVenue(@PathVariable UUID venueId) {
        venueService.deleteVenue(venueId);
        return ResponseEntity.noContent().build();
    }
}