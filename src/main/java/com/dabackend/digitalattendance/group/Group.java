package com.dabackend.digitalattendance.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    // A group can have many members. This defines the "one-to-many" side of the relationship.
    // `mappedBy = "group"` tells JPA that the GroupMember entity owns the relationship.
    // `cascade = CascadeType.ALL` means if we delete a group, all its memberships are also deleted.
    // `orphanRemoval = true` handles cases where a member is removed from the list.
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GroupMember> members = new ArrayList<>();
}