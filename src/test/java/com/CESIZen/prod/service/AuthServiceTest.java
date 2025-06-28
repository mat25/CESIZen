package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.TokenDTO;
import com.CESIZen.prod.dto.user.LoginDTO;
import com.CESIZen.prod.dto.user.RegisterDTO;
import com.CESIZen.prod.dto.user.RegisterWithRoleDTO;
import com.CESIZen.prod.entity.Role;
import com.CESIZen.prod.entity.RoleEnum;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.entity.UserStatusEnum;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.RoleRepository;
import com.CESIZen.prod.repository.UserRepository;
import com.CESIZen.prod.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private Authentication authentication;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        User user = new User();
        user.setUsername("testuser");
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setRole(new Role(RoleEnum.USER));
        user.setId(42L);

        when(userRepository.findByUsernameAndDeletedFalse("testuser")).thenReturn(user);
        when(jwtUtils.generateToken("testuser", RoleEnum.USER.name())).thenReturn("token123");

        TokenDTO tokenDTO = authService.login(loginDTO);

        assertNotNull(tokenDTO);
        assertEquals("token123", tokenDTO.getToken());
        assertEquals(42L, tokenDTO.getId());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsernameAndDeletedFalse("testuser");
        verify(jwtUtils).generateToken("testuser", RoleEnum.USER.name());
    }

    @Test
    void login_userNotFound_throwsNotFoundException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userRepository.findByUsernameAndDeletedFalse("testuser")).thenReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> authService.login(loginDTO));
        assertEquals("Utilisateur introuvable.", ex.getMessage());
    }

    @Test
    void login_userInactive_throwsBadRequestException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        User user = new User();
        user.setUsername("testuser");
        user.setStatus(UserStatusEnum.INACTIVE);

        when(userRepository.findByUsernameAndDeletedFalse("testuser")).thenReturn(user);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.login(loginDTO));
        assertEquals("Ce compte est désactivé. Veuillez contacter un administrateur.", ex.getMessage());
    }

    @Test
    void register_success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("password");

        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())).thenReturn(false);
        Role roleUser = new Role(RoleEnum.USER);
        when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        MessageDTO result = authService.register(dto);

        assertEquals("Utilisateur enregistré", result.getMessage());
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("newuser") &&
                        user.getEmail().equals("newuser@example.com") &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getStatus() == UserStatusEnum.ACTIVE &&
                        user.getRole().equals(roleUser)
        ));
    }

    @Test
    void register_emailAlreadyUsed_throwsBadRequestException() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@example.com");
        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(dto));
        assertEquals("Email déjà utilisé", ex.getMessage());
    }

    @Test
    void register_usernameAlreadyUsed_throwsBadRequestException() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test2@example.com");
        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(dto));
        assertEquals("Le username est déjà utilisé.", ex.getMessage());
    }

    @Test
    void register_roleUserNotFound_throwsBadRequestException() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("user");
        dto.setEmail("user@example.com");
        dto.setPassword("pass");

        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(dto));
        assertEquals("Rôle USER introuvable", ex.getMessage());
    }

    @Test
    void registerWithRole_success() {
        RegisterWithRoleDTO dto = new RegisterWithRoleDTO();
        dto.setUsername("adminuser");
        dto.setEmail("adminuser@example.com");
        dto.setPassword("password");
        dto.setRole(RoleEnum.ADMIN);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("superadmin");

        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())).thenReturn(false);
        Role role = new Role(RoleEnum.ADMIN);
        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPass");

        MessageDTO messageDTO = authService.registerWithRole(dto, auth);

        assertEquals("Utilisateur avec rôle ADMIN enregistré", messageDTO.getMessage());
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("adminuser") &&
                        user.getEmail().equals("adminuser@example.com") &&
                        user.getPassword().equals("encodedPass") &&
                        user.getStatus() == UserStatusEnum.ACTIVE &&
                        user.getRole().equals(role)
        ));
    }

    @Test
    void registerWithRole_emailAlreadyUsed_throwsBadRequestException() {
        RegisterWithRoleDTO dto = new RegisterWithRoleDTO();
        dto.setEmail("used@example.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");

        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.registerWithRole(dto, auth));
        assertEquals("Email déjà utilisé", ex.getMessage());
    }

    @Test
    void registerWithRole_usernameAlreadyUsed_throwsBadRequestException() {
        RegisterWithRoleDTO dto = new RegisterWithRoleDTO();
        dto.setUsername("usedusername");
        dto.setEmail("email@example.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");

        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.registerWithRole(dto, auth));
        assertEquals("Le username est déjà utilisé.", ex.getMessage());
    }

    @Test
    void registerWithRole_roleNotFound_throwsBadRequestException() {
        RegisterWithRoleDTO dto = new RegisterWithRoleDTO();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("pass");
        dto.setRole(RoleEnum.ADMIN);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");

        when(userRepository.existsByEmailAndDeletedFalse(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.registerWithRole(dto, auth));
        assertEquals("Rôle ADMIN introuvable", ex.getMessage());
    }
}
