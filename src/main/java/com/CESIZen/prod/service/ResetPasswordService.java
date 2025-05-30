package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.resetPassword.ResetPasswordConfirmDTO;
import com.CESIZen.prod.dto.resetPassword.ResetPasswordRequestDTO;
import com.CESIZen.prod.entity.*;
import com.CESIZen.prod.repository.PasswordResetTokenRepository;
import com.CESIZen.prod.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ResetPasswordService {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MessageDTO requestReset(ResetPasswordRequestDTO dto) {
        log.info("Demande de réinitialisation de mot de passe pour email={}", dto.getEmail());

        User user = userRepository.findByEmailAndDeletedFalse(dto.getEmail())
                .orElseThrow(() -> {
                    log.warn("Aucun compte associé à l'email {}", dto.getEmail());
                    return new RuntimeException("Aucun compte associé à cet email");
                });

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        tokenRepository.deleteByUserId(user.getId());
        log.info("Suppression des anciens tokens pour l'utilisateur id={}", user.getId());

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiry);
        tokenRepository.save(resetToken);
        log.info("Nouveau token de réinitialisation créé pour l'utilisateur id={}, token={}", user.getId(), token);

        // Envoyer email ici
        System.out.println("Lien de réinitialisation : https://frontend/reset-password?token=" + token);

        return new MessageDTO("Lien de réinitialisation envoyé à l'adresse email");
    }

    public MessageDTO confirmReset(ResetPasswordConfirmDTO dto) {
        log.info("Confirmation de réinitialisation du mot de passe avec token={}", dto.getToken());

        PasswordResetToken resetToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> {
                    log.warn("Token invalide : {}", dto.getToken());
                    return new RuntimeException("Token invalide");
                });

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Token expiré : {}", dto.getToken());
            throw new RuntimeException("Le lien de réinitialisation a expiré");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        log.info("Mot de passe réinitialisé avec succès pour l'utilisateur id={}", user.getId());

        return new MessageDTO("Mot de passe réinitialisé avec succès");
    }
}
