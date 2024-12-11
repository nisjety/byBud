package com.bybud.userservice.repository;

import com.bybud.userservice.model.AppRole;
import com.bybud.common.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByName(RoleName name);
    boolean existsByName(RoleName name);
}
