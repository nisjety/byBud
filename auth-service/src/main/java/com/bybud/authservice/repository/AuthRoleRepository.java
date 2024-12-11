package com.bybud.authservice.repository;

import com.bybud.authservice.model.AuthRole;
import com.bybud.common.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRoleRepository extends JpaRepository<AuthRole, Long> {
    Optional<AuthRole> findByName(RoleName name);
}

