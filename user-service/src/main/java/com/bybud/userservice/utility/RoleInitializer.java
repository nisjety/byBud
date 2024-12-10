package com.bybud.userservice.utility;

import com.bybud.userservice.model.AppRole;
import com.bybud.userservice.model.RoleName;
import com.bybud.userservice.repository.AppRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer {

    private final AppRoleRepository appRoleRepository;

    @Autowired
    public RoleInitializer(AppRoleRepository appRoleRepository) {
        this.appRoleRepository = appRoleRepository;
    }

    @PostConstruct
    public void initRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (!appRoleRepository.existsByName(roleName)) {
                AppRole role = new AppRole();
                role.setName(roleName);
                appRoleRepository.save(role);
            }
        }
    }
}
