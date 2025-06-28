package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.UpdatePasswordDTO;
import com.CESIZen.prod.dto.user.UpdateUserDTO;
import com.CESIZen.prod.dto.user.UserDTO;
import com.CESIZen.prod.entity.Role;
import com.CESIZen.prod.entity.RoleEnum;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.entity.UserStatusEnum;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticResultRepository;
import com.CESIZen.prod.repository.PasswordResetTokenRepository;
import com.CESIZen.prod.repository.UserRepository;
import com.CESIZen.prod.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private DiagnosticResultRepository diagnosticResultRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private Authentication authentication;

    private Role userRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRole = new Role();
        userRole.setName(RoleEnum.USER);
    }

    private User createUser(Long id, String username, String email, String rawPassword, UserStatusEnum status) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(rawPassword);
        user.setStatus(status);
        user.setRole(userRole);
        user.setDeleted(false);
        return user;
    }

    @Test
    void getAllUsers_success() {
        User user1 = createUser(1L, "user1", "user1@example.com", "encodedPwd1", UserStatusEnum.ACTIVE);
        User user2 = createUser(2L, "user2", "user2@example.com", "encodedPwd2", UserStatusEnum.ACTIVE);

        when(userRepository.findAllByDeletedFalse()).thenReturn(Arrays.asList(user1, user2));

        List<UserDTO> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u -> u.getUsername().startsWith("user")));
        verify(userRepository).findAllByDeletedFalse();
    }

    @Test
    void getCurrentUser_success() {
        User user = createUser(1L, "currentuser", "current@example.com", "encodedPwd", UserStatusEnum.ACTIVE);

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);

        UserDTO result = userService.getCurrentUser(authentication);

        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void updateCurrentUser_success() {
        User user = createUser(1L, "olduser", "old@example.com", "encodedPwd", UserStatusEnum.ACTIVE);
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUsername("newuser");
        updateDTO.setEmail("new@example.com");

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(userRepository.existsByUsernameAndDeletedFalse("newuser")).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedFalse("new@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserDTO updatedUser = userService.updateCurrentUser(authentication, updateDTO);

        assertEquals("newuser", updatedUser.getUsername());
        assertEquals("new@example.com", updatedUser.getEmail());
    }

    @Test
    void updateCurrentUser_failUsernameAlreadyUsed() {
        User user = createUser(1L, "olduser", "old@example.com", "encodedPwd", UserStatusEnum.ACTIVE);
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUsername("takenUsername");

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(userRepository.existsByUsernameAndDeletedFalse("takenUsername")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                userService.updateCurrentUser(authentication, updateDTO));
        assertEquals("Le username est déjà utilisé.", ex.getMessage());
    }

    @Test
    void updateCurrentUser_failEmailAlreadyUsed() {
        User user = createUser(1L, "olduser", "old@example.com", "encodedPwd", UserStatusEnum.ACTIVE);
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setEmail("taken@example.com");

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(userRepository.existsByUsernameAndDeletedFalse(anyString())).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedFalse("taken@example.com")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                userService.updateCurrentUser(authentication, updateDTO));
        assertEquals("Email déjà utilisé", ex.getMessage());
    }

    @Test
    void updatePassword_success() {
        User user = createUser(1L, "user", "user@example.com", "encodedOldPwd", UserStatusEnum.ACTIVE);
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("oldPwd");
        dto.setNewPassword("newPwd");

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(passwordEncoder.matches("oldPwd", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPwd")).thenReturn("encodedNewPwd");
        when(userRepository.save(any())).thenReturn(user);

        MessageDTO result = userService.updatePassword(authentication, dto);

        assertEquals("Mot de passe mis à jour avec succès", result.getMessage());
        verify(userRepository).save(user);
        assertEquals("encodedNewPwd", user.getPassword());
    }

    @Test
    void updatePassword_failOldPasswordIncorrect() {
        User user = createUser(1L, "user", "user@example.com", "encodedOldPwd", UserStatusEnum.ACTIVE);
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("wrongOldPwd");
        dto.setNewPassword("newPwd");

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(passwordEncoder.matches("wrongOldPwd", user.getPassword())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.updatePassword(authentication, dto));
        assertEquals("Ancien mot de passe incorrect", ex.getMessage());
    }

    @Test
    void deleteCurrentUser_success() {
        User user = createUser(1L, "user", "user@example.com", "encodedPwd", UserStatusEnum.ACTIVE);

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);

        MessageDTO result = userService.deleteCurrentUser(authentication);

        assertTrue(user.isDeleted());
        assertEquals("Compte supprimé", result.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_success() {
        User user = createUser(1L, "user", "user@example.com", "encodedPwd", UserStatusEnum.ACTIVE);

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.deactivateUser(1L);

        assertEquals(UserStatusEnum.INACTIVE, user.getStatus());
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void deactivateUser_failUserNotFound() {
        when(userRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.deactivateUser(999L));
        assertEquals("Utilisateur non trouvé", ex.getMessage());
    }

    @Test
    void activateUser_success() {
        User user = createUser(1L, "user", "user@example.com", "encodedPwd", UserStatusEnum.INACTIVE);

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.activateUser(1L);

        assertEquals(UserStatusEnum.ACTIVE, user.getStatus());
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void activateUser_failUserNotFound() {
        when(userRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.activateUser(999L));
        assertEquals("Utilisateur non trouvé", ex.getMessage());
    }

    @Test
    void deleteUserById_success() {
        User user = createUser(1L, "user", "user@example.com", "encodedPwd", UserStatusEnum.ACTIVE);

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        MessageDTO result = userService.deleteUserById(1L);

        assertTrue(user.isDeleted());
        assertEquals("Utilisateur supprimé", result.getMessage());
    }

    @Test
    void deleteUserById_failUserNotFound() {
        when(userRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.deleteUserById(999L));
        assertEquals("Utilisateur non trouvé", ex.getMessage());
    }

    @Test
    void exportUserData_success() throws Exception {
        User user = createUser(1L, "user", "user@example.com", "encodedPwd", UserStatusEnum.ACTIVE);

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        when(diagnosticResultRepository.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());

        byte[] data = userService.exportUserData(authentication);

        assertNotNull(data);
        assertTrue(data.length > 0);
    }

    @Test
    void requestDataDeletion_success() {
        User user = createUser(1L, "user", "user@example.com", "encodedPwd", UserStatusEnum.ACTIVE);

        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);
        doNothing().when(passwordResetTokenRepository).deleteAllByUser(user);
        doNothing().when(diagnosticResultRepository).deleteAllByUser(user);
        doNothing().when(userRepository).delete(user);

        MessageDTO result = userService.requestDataDeletion(authentication);

        assertEquals("Demande de suppression des données enregistrée", result.getMessage());
        verify(passwordResetTokenRepository).deleteAllByUser(user);
        verify(diagnosticResultRepository).deleteAllByUser(user);
        verify(userRepository).delete(user);
    }
}
