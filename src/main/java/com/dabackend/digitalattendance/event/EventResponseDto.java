package com.dabackend.digitalattendance.event;

import com.dabackend.digitalattendance.venue.VenueDto; // <-- NEW IMPORT
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class EventResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;

    // UPDATED: This now uses the full VenueDto, not the limited VenueInfo
    private VenueDto venue;

    private GroupInfo group;

    // The VenueInfo static class has been removed.

    @Data
    @Builder
    public static class GroupInfo {
        private UUID id;
        private String name;
    }
}