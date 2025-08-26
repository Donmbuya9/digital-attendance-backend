package com.dabackend.digitalattendance.event;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AttendanceCodeManager {
    // A thread-safe map to store active codes. In a real-world, multi-server application,
    // you would use a distributed cache like Redis instead of this in-memory map.
    private final ConcurrentHashMap<String, ActiveCode> activeCodes = new ConcurrentHashMap<>();
    private static final int CODE_EXPIRATION_SECONDS = 90;

    public record ActiveCode(String code, UUID eventId, Instant expiresAt) {}

    public ActiveCode generateCode(UUID eventId) {
        // Generate a simple, human-readable 6-digit code.
        String code = String.format("%03d-%03d",
                ThreadLocalRandom.current().nextInt(0, 1000),
                ThreadLocalRandom.current().nextInt(0, 1000));

        Instant expiresAt = Instant.now().plusSeconds(CODE_EXPIRATION_SECONDS);
        ActiveCode activeCode = new ActiveCode(code, eventId, expiresAt);

        // Remove any old code for this event to ensure only one is active at a time.
        activeCodes.values().removeIf(ac -> ac.eventId().equals(eventId));
        activeCodes.put(code, activeCode);

        return activeCode;
    }

    public boolean validateCode(UUID eventId, String code) {
        ActiveCode activeCode = activeCodes.get(code);
        if (activeCode == null) return false; // Code doesn't exist.

        // If the code has expired, remove it and return false.
        if (activeCode.expiresAt().isBefore(Instant.now())) {
            activeCodes.remove(code);
            return false;
        }

        // The code is valid and not expired, now check if it belongs to the correct event.
        return activeCode.eventId().equals(eventId);
    }
}