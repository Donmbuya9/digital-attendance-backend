package com.dabackend.digitalattendance.event;

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
    private VenueInfo venue;
    private GroupInfo group;

    @Data
    @Builder
    public static class VenueInfo {
        private UUID id;
        private String name;
    }

    @Data
    @Builder
    public static class GroupInfo {
        private UUID id;
        private String name;
    }
}