package com.medical.appointments.user;

import com.medical.appointments.exception.RoleNotAssignedException;
import com.medical.appointments.exception.UserMustHaveAtLeastOneRoleException;
import com.medical.appointments.exception.RoleAlreadyAssignedException;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private Role activeRole;

    public void setActiveRole(Role activeRole) {
        if (activeRole == null) {
            throw new IllegalArgumentException("activeRole cannot be null");
        }

        if (!roles.contains(activeRole)) {
            throw new RoleNotAssignedException();
        }

        this.activeRole = activeRole;
    }

    public void addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (this.roles.contains(role)) {
            throw new RoleAlreadyAssignedException();
        }

        this.roles.add(role);
    }

    public void removeRole(Role role) {
        if (!this.roles.contains(role)) {
            throw new RoleNotAssignedException();
        }

        if (this.roles.size() == 1) {
            throw new UserMustHaveAtLeastOneRoleException();
        }

        this.roles.remove(role);

        if (role.equals(this.activeRole)) {
            setActiveRole(this.roles.iterator().next());
        }
    }

    @Builder
    public User(String email, String password, String firstName, String lastName, Set<Role> roles, Role activeRole) {
        if (roles == null || roles.isEmpty()) {
            throw new UserMustHaveAtLeastOneRoleException();
        }

        if (activeRole == null) {
            throw new IllegalArgumentException("activeRole cannot be null");
        }

        if (!roles.contains(activeRole)) {
            throw new RoleNotAssignedException();
        }

        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles.addAll(roles);
        this.activeRole = activeRole;
    }

    @Setter
    @Column(nullable = false)
    private String firstName;

    @Setter
    @Column(nullable = false)
    private String lastName;
}
