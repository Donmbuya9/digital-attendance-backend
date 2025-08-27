package com.dabackend.digitalattendance.event;

import com.dabackend.digitalattendance.common.ApiResponse;
import com.dabackend.digitalattendance.group.Group;
import com.dabackend.digitalattendance.group.GroupMember;
import com.dabackend.digitalattendance.group.GroupRepository;
import com.dabackend.digitalattendance.user.User;
import com.dabackend.digitalattendance.user.UserRepository;
import com.dabackend.digitalattendance.venue.Venue;
import com.dabackend.digitalattendance.venue.VenueDto;
import com.dabackend.digitalattendance.venue.VenueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final GroupRepository groupRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserRepository userRepository;
    private final AttendanceCodeManager attendanceCodeManager;

    @Transactional
    public EventResponseDto createEvent(EventRequestDto requestDto) {
        Venue venue = venueRepository.findById(requestDto.getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found"));
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        Event event = Event.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .venue(venue)
                .group(group)
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .build();

        Event savedEvent = eventRepository.save(event);
        return mapToEventResponseDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToEventResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDetailDto getEventWithAttendance(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        Map<UUID, AttendanceRecord> recordsByUserId = event.getAttendanceRecords().stream()
                .collect(Collectors.toMap(ar -> ar.getUser().getId(), ar -> ar));

        List<AttendeeStatusDto> attendees = event.getGroup().getMembers().stream()
                .map(GroupMember::getUser)
                .map(user -> {
                    AttendanceRecord record = recordsByUserId.get(user.getId());
                    return AttendeeStatusDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .status(record != null ? record.getStatus() : AttendanceStatus.PENDING)
                            .markedAt(record != null ? record.getMarkedAt() : null)
                            .build();
                }).toList();

        VenueDto venueDto = VenueDto.builder()
                .id(event.getVenue().getId())
                .name(event.getVenue().getName())
                .latitude(event.getVenue().getLatitude())
                .longitude(event.getVenue().getLongitude())
                .radius(event.getVenue().getRadius())
                .build();

        EventResponseDto.GroupInfo groupInfo = EventResponseDto.GroupInfo.builder()
                .id(event.getGroup().getId())
                .name(event.getGroup().getName())
                .build();

        return EventDetailDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .venue(venueDto)
                .group(groupInfo)
                .attendees(attendees)
                .build();
    }

    public StartAttendanceResponse startAttendance(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        Instant now = Instant.now();
        if (now.isBefore(event.getStartTime()) || now.isAfter(event.getEndTime())) {
            throw new IllegalStateException("Event is not currently active.");
        }
        AttendanceCodeManager.ActiveCode activeCode = attendanceCodeManager.generateCode(eventId);
        return StartAttendanceResponse.builder()
                .attendanceCode(activeCode.code())
                .expiresAt(activeCode.expiresAt())
                .build();
    }

    @Transactional
    public void markAttendance(UUID eventId, MarkAttendanceRequest request, Principal principal) {
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        if (!attendanceCodeManager.validateCode(eventId, request.getAttendanceCode())) {
            throw new IllegalStateException("Invalid or expired attendance code.");
        }

        Venue venue = event.getVenue();
        double distance = calculateDistanceInMeters(
                request.getLatitude(), request.getLongitude(),
                venue.getLatitude(), venue.getLongitude()
        );
        if (distance > venue.getRadius()) {
            throw new IllegalStateException("You are outside the allowed radius for this venue.");
        }

        AttendanceRecord record = attendanceRecordRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElse(AttendanceRecord.builder().event(event).user(user).build());

        if (record.getStatus() == AttendanceStatus.PRESENT) {
            throw new IllegalStateException("Attendance has already been marked for this event.");
        }

        record.setStatus(AttendanceStatus.PRESENT);
        record.setMarkedAt(Instant.now());
        attendanceRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsForUser(Principal principal) {
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return eventRepository.findEventsByUserId(user.getId()).stream()
                .map(this::mapToEventResponseDto)
                .collect(Collectors.toList());
    }

    // NEW METHOD ADDED
    @Transactional
    public ApiResponse manualOverrideAttendance(UUID eventId, ManualOverrideRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        boolean isMember = event.getGroup().getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(request.getUserId()));

        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a member of the group for this event.");
        }

        AttendanceRecord record = attendanceRecordRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElse(AttendanceRecord.builder()
                        .event(event)
                        .user(user)
                        .build());

        record.setStatus(AttendanceStatus.PRESENT);
        record.setMarkedAt(Instant.now());
        attendanceRecordRepository.save(record);

        return new ApiResponse("Attendee successfully marked as PRESENT.");
    }

    private double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }

    private EventResponseDto mapToEventResponseDto(Event event) {
        VenueDto venueDto = VenueDto.builder()
                .id(event.getVenue().getId())
                .name(event.getVenue().getName())
                .latitude(event.getVenue().getLatitude())
                .longitude(event.getVenue().getLongitude())
                .radius(event.getVenue().getRadius())
                .build();

        EventResponseDto.GroupInfo groupInfo = EventResponseDto.GroupInfo.builder()
                .id(event.getGroup().getId())
                .name(event.getGroup().getName())
                .build();

        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .venue(venueDto)
                .group(groupInfo)
                .build();
    }
}