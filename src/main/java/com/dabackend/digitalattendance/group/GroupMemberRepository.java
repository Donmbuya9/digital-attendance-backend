package com.dabackend.digitalattendance.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // <-- ADD THIS IMPORT
import java.util.Optional;
import java.util.UUID;

@Repository // <-- ADD THIS ANNOTATION
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    // Custom query to check if a user is already in a group, which will be useful later.
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);

    // Custom query to find a specific membership record for deletion.
    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);
}