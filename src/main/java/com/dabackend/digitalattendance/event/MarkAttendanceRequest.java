package com.dabackend.digitalattendance.event;

import lombok.Data;

@Data
public class MarkAttendanceRequest {
    private String attendanceCode;
    private double latitude;
    private double longitude;
}