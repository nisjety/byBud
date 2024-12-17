package com.bybud.common.config;

import com.bybud.common.dto.UserDTO;
import com.bybud.common.model.RoleName;

public class RoleValidator {

    public static void validateRole(UserDTO user, RoleName requiredRole, String errorMessage) {
        if (!user.getRoles().contains(requiredRole)) {
            throw new SecurityException(errorMessage);
        }
    }

    public static boolean hasRole(UserDTO user, RoleName role) {
        return user.getRoles().contains(role);
    }
}
