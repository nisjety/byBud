package com.bybud.authservice.repository;

import com.bybud.authservice.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<AuthUser> findAllByActive(boolean active, Sort sort);

    Page<AuthUser> findAll(Pageable pageable);

    List<AuthUser> findAllByRoles_Name(String roleName);
}
