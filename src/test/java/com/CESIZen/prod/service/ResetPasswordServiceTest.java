package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.resetPassword.ResetPasswordConfirmDTO;
import com.CESIZen.prod.dto.resetPassword.ResetPasswordRequestDTO;
import com.CESIZen.prod.entity.PasswordResetToken;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.repository.PasswordResetTokenRepository;
import com.CESIZen.prod.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("oldPassword");
    }

    @Test
    void requestReset_shouldCreateAndSendToken_whenUserExists() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setEmail("test@example.com");

        when(userRepository.findByEmailAndDeletedFalse(dto.getEmail()))
                .thenReturn(Optional.of(user));

        MessageDTO result = resetPasswordService.requestReset(dto);

        verify(tokenRepository).deleteByUserId(user.getId());
        verify(tokenRepository).flush();
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendResetPasswordEmail(eq(user.getEmail()), anyString());
        assertEquals("Lien de réinitialisation envoyé à l'adresse email", result.getMessage());
    }

    @Test
    void requestReset_shouldThrowException_whenUserNotFound() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setEmail("notfound@example.com");

        when(userRepository.findByEmailAndDeletedFalse(dto.getEmail()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resetPasswordService.requestReset(dto);
        });

        assertEquals("Aucun compte associé à cet email", exception.getMessage());
    }

    @Test
    void confirmReset_shouldResetPassword_whenTokenIsValid() {
        String token = UUID.randomUUID().toString();
        ResetPasswordConfirmDTO dto = new ResetPasswordConfirmDTO();
        dto.setToken(token);
        dto.setNewPassword("newPass123");

        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("encodedPassword");

        MessageDTO result = resetPasswordService.confirmReset(dto);

        verify(userRepository).save(user);
        verify(tokenRepository).delete(resetToken);
        assertEquals("Mot de passe réinitialisé avec succès", result.getMessage());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void confirmReset_shouldThrowException_whenTokenNotFound() {
        ResetPasswordConfirmDTO dto = new ResetPasswordConfirmDTO();
        dto.setToken("invalidToken");

        when(tokenRepository.findByToken(dto.getToken()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resetPasswordService.confirmReset(dto);
        });

        assertEquals("Token invalide", exception.getMessage());
    }

    @Test
    void confirmReset_shouldThrowException_whenTokenExpired() {
        String token = UUID.randomUUID().toString();
        ResetPasswordConfirmDTO dto = new ResetPasswordConfirmDTO();
        dto.setToken(token);
        dto.setNewPassword("newPass123");

        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resetPasswordService.confirmReset(dto);
        });

        assertEquals("Le lien de réinitialisation a expiré", exception.getMessage());
    }
}
