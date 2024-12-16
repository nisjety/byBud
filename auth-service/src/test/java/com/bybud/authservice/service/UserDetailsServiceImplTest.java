package com.bybud.authservice.service;

import com.bybud.common.security.UserDetailsServiceImpl;
import com.bybud.common.model.Role;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("auth-service")
public class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testLoadUserByUsername_Success() {
        // Arrange
        User mockUser = new User("testuser", "test@example.com", "password123", "Test User", LocalDate.now(), Set.of(new Role(RoleName.ROLE_CUSTOMER)));
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        Mockito.verify(userRepository).findByUsername("testuser");
    }

    @Test
    public void testLoadUserByUsername_NotFound() {
        // Arrange
        Mockito.when(userRepository.findByUsername("invaliduser")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("invaliduser"));

        assertEquals("User not found with username or email: invaliduser", exception.getMessage());
        Mockito.verify(userRepository).findByUsername("invaliduser");
    }
}

