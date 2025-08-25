package com.dabackend.digitalattendance.group;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto) {
        return new ResponseEntity<>(groupService.createGroup(groupDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailDto> getGroupById(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupWithMembers(groupId));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(@PathVariable UUID groupId, @RequestBody AddMemberRequest request) {
        groupService.addMemberToGroup(groupId, request.getUserId());
        // Per API contract, a success message is returned. A 200 OK is sufficient.
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID groupId, @PathVariable UUID userId) {
        groupService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }
}