package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.LoginDTO;
import com.CESIZen.prod.dto.user.RegisterDTO;
import com.CESIZen.prod.dto.user.RegisterWithRoleDTO;
import com.CESIZen.prod.entity.*;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.RoleRepository;
import com.CESIZen.prod.repository.UserRepository;
import com.CESIZen.prod.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public TokenDTO login(LoginDTO request) {
        log.info("Tentative de connexion pour l'utilisateur '{}'", request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsernameAndDeletedFalse(userDetails.getUsername());

        if (user == null) {
            log.warn("Échec de connexion : utilisateur '{}' introuvable", userDetails.getUsername());
            throw new NotFoundException("Utilisateur introuvable.");
        }

        if (user.getStatus() == UserStatusEnum.INACTIVE) {
            log.warn("Échec de connexion : compte utilisateur '{}' désactivé", user.getUsername());
            throw new BadRequestException("Ce compte est désactivé. Veuillez contacter un administrateur.");
        }

        String role = user.getRole().getName().name();
        String token = jwtUtils.generateToken(user.getUsername(), role);

        log.info("Connexion réussie pour l'utilisateur '{}', rôle '{}'", user.getUsername(), role);
        return new TokenDTO(token, user.getId());
    }

    public MessageDTO register(RegisterDTO dto) {
        log.info("Inscription d'un nouvel utilisateur avec username '{}' et email '{}'", dto.getUsername(), dto.getEmail());
        if (userRepository.existsByEmailAndDeletedFalse(dto.getEmail())) {
            log.warn("Inscription échouée : email '{}' déjà utilisé", dto.getEmail());
            throw new BadRequestException("Email déjà utilisé");
        }
        if (userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())) {
            log.warn("Inscription échouée : username '{}' déjà utilisé", dto.getUsername());
            throw new BadRequestException("Le username est déjà utilisé.");
        }

        Role userRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> {
                    log.error("Rôle USER introuvable lors de l'inscription de '{}'", dto.getUsername());
                    return new BadRequestException("Rôle USER introuvable");
                });

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setRole(userRole);

        userRepository.save(user);
        log.info("Nouvel utilisateur '{}' enregistré avec succès", dto.getUsername());
        return new MessageDTO("Utilisateur enregistré");
    }

    public MessageDTO registerWithRole(RegisterWithRoleDTO dto, Authentication authentication) {
        String adminUsername = authentication.getName();
        log.info("Admin '{}' inscrit un utilisateur avec rôle '{}', username '{}', email '{}'",
                adminUsername, dto.getRole(), dto.getUsername(), dto.getEmail());

        if (userRepository.existsByEmailAndDeletedFalse(dto.getEmail())) {
            log.warn("Inscription échouée : email '{}' déjà utilisé par admin '{}'", dto.getEmail(), adminUsername);
            throw new BadRequestException("Email déjà utilisé");
        }
        if (userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())) {
            log.warn("Inscription échouée : username '{}' déjà utilisé par admin '{}'", dto.getUsername(), adminUsername);
            throw new BadRequestException("Le username est déjà utilisé.");
        }

        if (dto.getRole() != RoleEnum.USER && dto.getRole() != RoleEnum.ADMIN) {
            log.warn("Inscription échouée : rôle non autorisé '{}' pour l'utilisateur '{}' par admin '{}'",
                    dto.getRole(), dto.getUsername(), adminUsername);
            throw new BadRequestException("Seuls les rôles USER ou ADMIN peuvent être attribués.");
        }

        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> {
                    log.error("Rôle '{}' introuvable lors de l'inscription de '{}' par admin '{}'",
                            dto.getRole(), dto.getUsername(), adminUsername);
                    return new BadRequestException("Rôle " + dto.getRole() + " introuvable");
                });

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setRole(role);

        userRepository.save(user);
        log.info("Admin '{}' a enregistré avec succès l'utilisateur '{}' avec rôle '{}'", adminUsername, dto.getUsername(), dto.getRole());
        return new MessageDTO("Utilisateur avec rôle " + dto.getRole() + " enregistré");
    }

}
