package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.UpdateUserDTO;
import com.CESIZen.prod.dto.user.UserDTO;
import com.CESIZen.prod.entity.RoleEnum;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.entity.UserStatusEnum;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.DiagnosticResultRepository;
import com.CESIZen.prod.repository.PasswordResetTokenRepository;
import com.CESIZen.prod.repository.UserRepository;
import com.CESIZen.prod.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

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
        return userRepository.findAllByDeletedFalse().stream()
                .filter(user -> user.getRole().getName() == RoleEnum.USER)
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO getCurrentUser(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        return new UserDTO(user);
    }

    public UserDTO updateCurrentUser(Authentication authentication, UpdateUserDTO updateDTO) {
        User user = securityUtils.getCurrentUser(authentication);

        if (updateDTO.getUsername() != null)
            user.setUsername(updateDTO.getUsername());

        if (updateDTO.getEmail() != null)
            user.setEmail(updateDTO.getEmail());

        if (updateDTO.getPassword() != null)
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));

        userRepository.save(user);
        return new UserDTO(user);
    }

    public MessageDTO deleteCurrentUser(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);
        user.setDeleted(true);
        userRepository.save(user);
        return new MessageDTO("Compte supprimé");
    }

    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        user.setStatus(UserStatusEnum.INACTIVE);
        userRepository.save(user);
        return new UserDTO(user);
    }

    public UserDTO activateUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        user.setStatus(UserStatusEnum.ACTIVE);
        userRepository.save(user);
        return new UserDTO(user);
    }

    public MessageDTO deleteUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        user.setDeleted(true);
        userRepository.save(user);
        return new MessageDTO("Utilisateur supprimé");
    }

    public byte[] exportUserData(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsBytes(new UserDTO(user));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export des données utilisateur", e);
        }
    }

    @Transactional
    public MessageDTO requestDataDeletion(Authentication authentication) {
        User user = securityUtils.getCurrentUser(authentication);

        passwordResetTokenRepository.deleteAllByUser(user);

        diagnosticResultRepository.deleteAllByUser(user);

        userRepository.delete(user);
        return new MessageDTO("Demande de suppression des données enregistrée");
    }
}
