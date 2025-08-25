package com.dabackend.digitalattendance.group;

import lombok.Data;
import java.util.UUID;

@Data
public class AddMemberRequest {
    private UUID userId;
}