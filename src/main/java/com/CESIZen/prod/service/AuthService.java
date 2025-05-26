package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.LoginDTO;
import com.CESIZen.prod.dto.user.RegisterDTO;
import com.CESIZen.prod.entity.*;
import com.CESIZen.prod.exception.BadRequestException;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.RoleRepository;
import com.CESIZen.prod.repository.UserRepository;
import com.CESIZen.prod.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

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

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BadRequestException("Le username est requis.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Le mot de passe est requis.");
        }

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

    public MessageDTO register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email déjà utilisé");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Le username est déjà utilisé.");
        }

        Role userRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new BadRequestException("Rôle USER introuvable"));

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        User user = new User(dto.getUsername(), dto.getEmail(), hashedPassword, userRole);

        userRepository.save(user);
        return new MessageDTO("Utilisateur enregistré");
    }
}
