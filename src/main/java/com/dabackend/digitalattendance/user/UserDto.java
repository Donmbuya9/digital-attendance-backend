package com.dabackend.digitalattendance.user;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}