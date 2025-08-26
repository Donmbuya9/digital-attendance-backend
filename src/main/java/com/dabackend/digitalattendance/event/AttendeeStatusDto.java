package com.dabackend.digitalattendance.event;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AttendeeStatusDto {
    private UUID id; // This is the User ID
    private String firstName;
    private String lastName;
    private String email;
    private AttendanceStatus status;
    private Instant markedAt;
}