package com.dabackend.digitalattendance.venue;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

// @Data is a convenient Lombok annotation that bundles @Getter, @Setter, @ToString, etc.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Venue name must be unique and has a max length of 100 characters.
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Latitude and Longitude are stored as double-precision floating-point numbers.
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // Radius for geolocation check, stored in meters.
    @Column(nullable = false)
    private Integer radius;
}