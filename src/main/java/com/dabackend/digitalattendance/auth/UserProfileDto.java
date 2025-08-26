package com.dabackend.digitalattendance.auth;

import com.dabackend.digitalattendance.user.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserProfileDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}