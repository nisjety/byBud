package com.bybud.common.repository;

import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    Optional<User> findByUsername(String username);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check if a username already exists
    boolean existsByUsername(String username);

    // Find user by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Check if an email already exists
    boolean existsByEmail(String email);

    // Find all active or inactive users with sorting
    List<User> findAllByActive(boolean active, Sort sort);

    // Paginated fetching of all users
    Page<User> findAll(Pageable pageable);

    // Find all users by a specific role
    List<User> findAllByRoles(RoleName role);
}
