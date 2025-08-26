package com.dabackend.digitalattendance.event;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EventDetailDto {
    private UUID id;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private EventResponseDto.VenueInfo venue;
    private EventResponseDto.GroupInfo group;
    private List<AttendeeStatusDto> attendees;
}