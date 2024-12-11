package com.bybud.userservice.model;

import com.bybud.common.model.RoleName;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "app_role", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<AppUser> users;

    // Constructors
    public AppRole() {
    }

    public AppRole(RoleName name) {
        this.name = name;
    }

    // Getters and Setters
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

    public Set<AppUser> getUsers() {
        return users;
    }

    public void setUsers(Set<AppUser> users) {
        this.users = users;
    }

    // toString (Exclude users to avoid recursion)
    @Override
    public String toString() {
        return "AppRole{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
