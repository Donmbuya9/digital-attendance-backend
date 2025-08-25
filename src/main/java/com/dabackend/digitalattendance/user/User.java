package com.dabackend.digitalattendance.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
// IMPLEMENT THE UserDetails INTERFACE
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // --- UserDetails METHODS (NEWLY ADDED) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // This tells Spring Security what role the user has.
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // In our system, the email is the username.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // We don't have account expiration logic.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // We don't have account locking logic.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // We don't have password expiration logic.
    }

    @Override
    public boolean isEnabled() {
        return true; // All our users are enabled by default.
    }
}