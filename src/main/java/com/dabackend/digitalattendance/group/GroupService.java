package com.dabackend.digitalattendance.group;

import com.dabackend.digitalattendance.user.User;
import com.dabackend.digitalattendance.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    // Create a new group
    public GroupDto createGroup(GroupDto groupDto) {
        groupRepository.findByName(groupDto.getName()).ifPresent(g -> {
            throw new IllegalStateException("Group with name '" + groupDto.getName() + "' already exists.");
        });
        Group group = Group.builder()
                .name(groupDto.getName())
                .description(groupDto.getDescription())
                .build();
        Group savedGroup = groupRepository.save(group);
        return mapToGroupDto(savedGroup);
    }

    // Get all groups (simple view)
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToGroupDto)
                .collect(Collectors.toList());
    }

    // Get group details with members
    @Transactional(readOnly = true) // Ensures lazy-loaded members are fetched
    public GroupDetailDto getGroupWithMembers(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        List<MemberDto> members = group.getMembers().stream()
                .map(groupMember -> mapToMemberDto(groupMember.getUser()))
                .collect(Collectors.toList());

        return GroupDetailDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .members(members)
                .build();
    }

    // Add a member to a group
    public void addMemberToGroup(UUID groupId, UUID userId) {
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new IllegalStateException("User is already a member of this group.");
        }
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        GroupMember membership = GroupMember.builder().group(group).user(user).build();
        groupMemberRepository.save(membership);
    }

    // Remove a member from a group
    @Transactional
    public void removeMemberFromGroup(UUID groupId, UUID userId) {
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found for the given group and user."));
        groupMemberRepository.delete(membership);
    }

    // Delete a group
    public void deleteGroup(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new EntityNotFoundException("Group not found with id: " + groupId);
        }
        groupRepository.deleteById(groupId);
    }

    // --- Helper Mappers ---
    private GroupDto mapToGroupDto(Group group) {
        return GroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .build();
    }

    private MemberDto mapToMemberDto(User user) {
        return MemberDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}