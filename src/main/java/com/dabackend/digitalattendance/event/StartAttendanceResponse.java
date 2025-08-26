package com.dabackend.digitalattendance.event;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class StartAttendanceResponse {
    private String attendanceCode;
    private Instant expiresAt;
}