package com.bybud.authservice.model;

import com.bybud.common.model.RoleName; // Import RoleName from user-service
import jakarta.persistence.*;

@Entity
@Table(name = "auth_role")
public class AuthRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name; // Use RoleName enum instead of String

    // Constructors
    public AuthRole() {}

    public AuthRole(RoleName name) {
        this.name = name;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}

