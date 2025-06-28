package com.CESIZen.prod.service;

import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceTest {

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        // Arrange
        String username = "testuser";
        String password = "securepassword";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(password);

        when(userRepository.findByUsernameAndDeletedFalse(username)).thenReturn(mockUser);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "unknownuser";
        when(userRepository.findByUsernameAndDeletedFalse(username)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals("Utilisateur introuvable pour le username : " + username, exception.getMessage());
    }
}
