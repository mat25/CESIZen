package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.diagnostic.DiagnosticExportDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticResultDTO;
import com.CESIZen.prod.dto.user.UpdatePasswordDTO;
import com.CESIZen.prod.dto.user.UpdateUserDTO;
import com.CESIZen.prod.dto.user.UserDTO;
import com.CESIZen.prod.dto.user.UserExportDTO;
import com.CESIZen.prod.entity.RoleEnum;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.entity.UserStatusEnum;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticResultRepository;
import com.CESIZen.prod.repository.PasswordResetTokenRepository;
import com.CESIZen.prod.repository.UserRepository;
import com.CESIZen.prod.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DiagnosticResultRepository diagnosticResultRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SecurityUtils securityUtils,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       DiagnosticResultRepository diagnosticResultRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.diagnosticResultRepository = diagnosticResultRepository;
    }

    public List<UserDTO> getAllUsers() {
        logger.info("Récupération de la liste des utilisateurs actifs avec rôle USER");
        List<UserDTO> users = userRepository.findAllByDeletedFalse().stream()
                .filter(user -> user.getRole().getName() == RoleEnum.USER)
                .map(UserDTO::new)
                .collect(Collectors.toList());
        logger.info("{} utilisateurs trouvés", users.size());
        return users;
    }

    public UserDTO getCurrentUser(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        logger.info("Récupération des informations de l'utilisateur courant : {}", user.getUsername());
        return new UserDTO(user);
    }

    public UserDTO updateCurrentUser(Authentication authentication, UpdateUserDTO updateDTO) {
        User user = securityUtils.getCurrentUser(authentication);
        logger.info("Mise à jour des informations de l'utilisateur courant : {}", user.getUsername());

        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndDeletedFalse(updateDTO.getUsername())) {
                logger.warn("Mise à jour échouée : username '{}' déjà utilisé", updateDTO.getUsername());
                throw new BadRequestException("Le username est déjà utilisé.");
            }
            logger.info("Mise à jour du username : {}", updateDTO.getUsername());
            user.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndDeletedFalse(updateDTO.getEmail())) {
                logger.warn("Mise à jour échouée : email '{}' déjà utilisé", updateDTO.getEmail());
                throw new BadRequestException("Email déjà utilisé");
            }
            logger.info("Mise à jour de l'email : {}", updateDTO.getEmail());
            user.setEmail(updateDTO.getEmail());
        }

        userRepository.save(user);
        logger.info("Utilisateur {} mis à jour avec succès", user.getUsername());
        return new UserDTO(user);
    }

    public MessageDTO updatePassword(Authentication authentication, UpdatePasswordDTO dto) {
        User user = securityUtils.getCurrentUser(authentication);

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        logger.info("Mot de passe mis à jour pour l'utilisateur {}", user.getUsername());
        return new MessageDTO("Mot de passe mis à jour avec succès");
    }


    public MessageDTO deleteCurrentUser(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        logger.info("Suppression logique du compte utilisateur courant : {}", user.getUsername());
        user.setDeleted(true);
        userRepository.save(user);
        logger.info("Compte utilisateur {} marqué comme supprimé", user.getUsername());
        return new MessageDTO("Compte supprimé");
    }

    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.warn("Tentative de désactivation d'un utilisateur non trouvé : id={}", id);
                    return new NotFoundException("Utilisateur non trouvé");
                });
        user.setStatus(UserStatusEnum.INACTIVE);
        userRepository.save(user);
        logger.info("Utilisateur {} désactivé", user.getUsername());
        return new UserDTO(user);
    }

    public UserDTO activateUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.warn("Tentative d'activation d'un utilisateur non trouvé : id={}", id);
                    return new NotFoundException("Utilisateur non trouvé");
                });
        user.setStatus(UserStatusEnum.ACTIVE);
        userRepository.save(user);
        logger.info("Utilisateur {} activé", user.getUsername());
        return new UserDTO(user);
    }

    public MessageDTO deleteUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.warn("Tentative de suppression d'un utilisateur non trouvé : id={}", id);
                    return new NotFoundException("Utilisateur non trouvé");
                });
        user.setDeleted(true);
        userRepository.save(user);
        logger.info("Utilisateur {} supprimé logiquement", user.getUsername());
        return new MessageDTO("Utilisateur supprimé");
    }

    public byte[] exportUserData(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        logger.info("Export des données utilisateur pour : {}", user.getUsername());

        try {
            List<DiagnosticExportDTO> diagnostics = diagnosticResultRepository
                    .findAllByUserId(user.getId())
                    .stream()
                    .map(DiagnosticExportDTO::new)
                    .collect(Collectors.toList());

            UserExportDTO exportDTO = new UserExportDTO(user, diagnostics);

            ObjectMapper mapper = new ObjectMapper();
            byte[] data = mapper.writeValueAsBytes(exportDTO);

            logger.info("Export des données utilisateur {} réussi", user.getUsername());
            return data;
        } catch (Exception e) {
            logger.error("Erreur lors de l'export des données utilisateur {} : {}", user.getUsername(), e.getMessage());
            throw new RuntimeException("Erreur lors de l'export des données utilisateur", e);
        }
    }


    @Transactional
    public MessageDTO requestDataDeletion(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        logger.info("Demande de suppression complète des données utilisateur : {}", user.getUsername());

        passwordResetTokenRepository.deleteAllByUser(user);
        logger.info("Tokens de réinitialisation du mot de passe supprimés pour {}", user.getUsername());

        diagnosticResultRepository.deleteAllByUser(user);
        logger.info("Résultats du diagnostic supprimés pour {}", user.getUsername());

        userRepository.delete(user);
        logger.info("Utilisateur {} supprimé définitivement", user.getUsername());

        return new MessageDTO("Demande de suppression des données enregistrée");
    }
}
