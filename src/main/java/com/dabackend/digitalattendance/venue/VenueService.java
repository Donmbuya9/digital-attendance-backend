package com.dabackend.digitalattendance.venue;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    // Create a new venue
    public VenueDto createVenue(VenueDto venueDto) {
        venueRepository.findByName(venueDto.getName()).ifPresent(v -> {
            throw new IllegalStateException("Venue with name '" + venueDto.getName() + "' already exists.");
        });

        Venue venue = Venue.builder()
                .name(venueDto.getName())
                .latitude(venueDto.getLatitude())
                .longitude(venueDto.getLongitude())
                .radius(venueDto.getRadius())
                .build();

        Venue savedVenue = venueRepository.save(venue);
        return mapToDto(savedVenue);
    }

    // Get all venues
    public List<VenueDto> getAllVenues() {
        return venueRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Get a single venue by ID
    public VenueDto getVenueById(UUID id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with id: " + id));
        return mapToDto(venue);
    }

    // Update a venue
    public VenueDto updateVenue(UUID id, VenueDto venueDto) {
        Venue existingVenue = venueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with id: " + id));

        existingVenue.setName(venueDto.getName());
        existingVenue.setLatitude(venueDto.getLatitude());
        existingVenue.setLongitude(venueDto.getLongitude());
        existingVenue.setRadius(venueDto.getRadius());

        Venue updatedVenue = venueRepository.save(existingVenue);
        return mapToDto(updatedVenue);
    }

    // Delete a venue
    public void deleteVenue(UUID id) {
        if (!venueRepository.existsById(id)) {
            throw new EntityNotFoundException("Venue not found with id: " + id);
        }
        venueRepository.deleteById(id);
    }

    // Helper method to map an Entity to a DTO
    private VenueDto mapToDto(Venue venue) {
        return VenueDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .radius(venue.getRadius())
                .build();
    }
}