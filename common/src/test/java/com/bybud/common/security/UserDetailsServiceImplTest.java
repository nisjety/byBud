package com.bybud.common.security;

import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");
        mockUser.setFullName("Test User");
        mockUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        mockUser.setPhoneNumber("1234567890");
        mockUser.setActive(true);
        mockUser.setRoles(Set.of(RoleName.CUSTOMER));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        assertTrue(userDetails.isEnabled());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));

        assertEquals("User not found with username or email: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
        verify(userRepository).findByEmail("nonexistent");
    }

    @Test
    void testLoadUserByUsername_UserIsInactive() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("inactiveuser");
        mockUser.setEmail("inactive@example.com");
        mockUser.setPassword("password123");
        mockUser.setActive(false); // User is inactive
        mockUser.setRoles(Set.of(RoleName.CUSTOMER));

        when(userRepository.findByUsername("inactiveuser")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("inactiveuser");

        // Assert
        assertFalse(userDetails.isEnabled());
        verify(userRepository).findByUsername("inactiveuser");
    }
}
