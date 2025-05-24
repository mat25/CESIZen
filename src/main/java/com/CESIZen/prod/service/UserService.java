package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.TokenDTO;
import com.CESIZen.prod.dto.user.*;
import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.*;
import com.CESIZen.prod.entity.Role;
import com.CESIZen.prod.entity.RoleEnum;
import com.CESIZen.prod.entity.UserStatusEnum;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.security.JwtUtil;
import com.CESIZen.prod.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtil jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityUtils securityUtils;

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private void validateRegistration(RegisterDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Le nom est requis.");
        }
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new BadRequestException("Le prénom est requis.");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BadRequestException("Le username est requis.");
        }
        if (request.getEmail() == null || !request.getEmail().matches(EMAIL_REGEX)) {
            throw new BadRequestException("Format d'email invalide.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new BadRequestException("Le mot de passe doit contenir au moins 8 caractères.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("L'email est déjà utilisé.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Le username est déjà utilisé.");
        }
    }


    public UserDTO createUser(RegisterDTO request) {
        validateRegistration(request);

        Role role = roleService.findByName(RoleEnum.USER)
                .orElseThrow(() -> new NotFoundException("Rôle USER introuvable."));

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getFirstName(),
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                role
        );

        log.info("Création d'un nouvel utilisateur : {}", request.getUsername());
        return new UserDTO(userRepository.save(user));
    }

    public UserDTO createUserWithRole(RegisterWithRoleDTO request) {
        validateRegistration(request);

        if (request.getRole() == null) {
            throw new BadRequestException("Le rôle est requis.");
        }

        RoleEnum roleEnum = request.getRole();

        Role role = roleService.findByName(roleEnum)
                .orElseThrow(() -> new NotFoundException("Rôle " + roleEnum.name() + " introuvable."));

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getFirstName(),
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                role
        );

        log.info("Création d'un utilisateur avec le rôle {} : {}", roleEnum.name(), request.getUsername());
        return new UserDTO(userRepository.save(user));
    }

    public TokenDTO loginUser(LoginDTO request) {

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BadRequestException("Le username est requis.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Le mot de passe est requis.");
        }

        log.info("Tentative de connexion pour l'utilisateur : {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername());

        if (user == null) {
            throw new NotFoundException("Utilisateur introuvable.");
        }

        if (user.getStatus() == UserStatusEnum.INACTIVE) {
            throw new BadRequestException("Ce compte est désactivé. Veuillez contacter un administrateur.");
        }

        String role = user.getRole().getName().name();
        String token = jwtUtils.generateToken(user.getUsername(), role);

        return new TokenDTO(token, user.getId());
    }

    public UserDTO getUserById(Long id) {
        log.info("Récupération de l'utilisateur avec l'ID : {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("L'utilisateur avec l'ID '" + id + "' n'existe pas."));

        Role role = user.getRole();
        if (role == null || role.getName() != RoleEnum.USER) {
            throw new BadRequestException("Seuls les utilisateurs avec le rôle USER peuvent être consultés.");
        }

        return new UserDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        log.info("Récupération de la liste complète des utilisateurs");
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .toList();
    }

    public UserDTO updateUser(Long id, UpdateUserDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("L'utilisateur '" + id + "' n'existe pas."));

        Role role = user.getRole();

        if (role == null || role.getName() != RoleEnum.USER) {
            throw new BadRequestException("Seuls les utilisateurs avec le rôle USER peuvent être modifié.");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getUsername() != null && !request.getUsername().isBlank()) {

            if (userRepository.existsByUsername(request.getUsername()) && !user.getUsername().equals(request.getUsername())) {
                throw new BadRequestException("Le username est déjà utilisé.");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            if (!request.getEmail().matches(EMAIL_REGEX)) {
                throw new BadRequestException("Format d'email invalide.");
            }

            if (userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
                throw new BadRequestException("L'email est déjà utilisé.");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {

            if (request.getPassword().length() < 8) {
                throw new BadRequestException("Le mot de passe doit contenir au moins 8 caractères.");
            }

            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        log.info("Mise à jour de l'utilisateur avec l'ID : {}", id);

        return new UserDTO(userRepository.save(user));
    }

    public UserDTO getCurrentUser(Authentication authentication) {

        User currentUser = securityUtils.getCurrentUser(authentication);

        return new UserDTO(currentUser);
    }

    public UserDTO updateCurrentUser(Authentication authentication, UpdateUserDTO request) {

        User currentUser = securityUtils.getCurrentUser(authentication);

        log.info("Mise à jour du compte de l'utilisateur connecté : {}", currentUser.getUsername());

        return updateUser(currentUser.getId(), request);
    }

    public MessageDTO deleteCurrentUser(Authentication authentication) {

        User currentUser = securityUtils.getCurrentUser(authentication);

        userRepository.delete(currentUser);

        log.warn("Suppression du compte de l'utilisateur connecté : {}", currentUser.getUsername());

        return new MessageDTO("Utilisateur supprimé avec succès.");
    }

    public MessageDTO deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("L'utilisateur '" + id + "' n'existe pas."));

        Role role = user.getRole();

        if (role == null || role.getName() != RoleEnum.USER) {
            throw new BadRequestException("Seuls les utilisateurs avec le rôle USER peuvent être supprimés.");
        }

        userRepository.delete(user);

        log.warn("Suppression de l'utilisateur avec l'ID : {}", id);

        return new MessageDTO("Utilisateur supprimé avec succès.");
    }

    public UserDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("L'utilisateur '" + id + "' n'existe pas."));

        if (user.getRole() == null || user.getRole().getName() != RoleEnum.USER) {
            throw new BadRequestException("Seuls les utilisateurs avec le rôle USER peuvent être désactivés ou réactivés.");
        }

        if (user.getStatus() == UserStatusEnum.ACTIVE) {
            user.setStatus(UserStatusEnum.INACTIVE);
            log.info("Désactivation de l'utilisateur avec l'ID : {}", id);
        } else {
            user.setStatus(UserStatusEnum.ACTIVE);
            log.info("Réactivation de l'utilisateur avec l'ID : {}", id);
        }

        return new UserDTO(userRepository.save(user));
    }
}
