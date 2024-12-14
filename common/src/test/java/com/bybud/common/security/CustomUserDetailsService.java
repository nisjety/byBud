package com.bybud.common.security;

import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.model.Role;
import com.bybud.common.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        // Add a role to the user
        Role customerRole = new Role();
        customerRole.setName(RoleName.ROLE_CUSTOMER);
        user.setRoles(Set.of(customerRole));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals("testuser", userDetails.getUsername(), "Username should match");
        assertEquals("testpassword", userDetails.getPassword(), "Password should match");
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER")),
                "Authorities should contain ROLE_CUSTOMER"
        );

        verify(userRepository, times(1)).findByUsername("testuser");
    }


    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknownuser"),
                "Should throw UsernameNotFoundException for non-existent user"
        );
        assertEquals("User not found with username or email: unknownuser", exception.getMessage());

        verify(userRepository, times(1)).findByUsername("unknownuser");
    }
}
