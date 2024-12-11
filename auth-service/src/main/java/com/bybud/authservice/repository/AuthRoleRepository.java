package com.bybud.authservice.repository;

import com.bybud.authservice.model.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AuthRoleRepository extends JpaRepository<AuthRole, Long> {
    Optional<AuthRole> findByName(String name);

    List<AuthRole> findAllByNameIn(Collection<String> names);
}
