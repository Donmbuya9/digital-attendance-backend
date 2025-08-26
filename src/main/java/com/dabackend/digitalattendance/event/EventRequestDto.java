package com.dabackend.digitalattendance.event;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class EventRequestDto {
    private String title;
    private String description;
    private UUID venueId;
    private UUID groupId;
    private Instant startTime;
    private Instant endTime;
}