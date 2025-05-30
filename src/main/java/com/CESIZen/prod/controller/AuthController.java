package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.LoginDTO;
import com.CESIZen.prod.dto.user.RegisterDTO;
import com.CESIZen.prod.dto.user.RegisterWithRoleDTO;
import com.CESIZen.prod.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Connexion d’un utilisateur", description = "Retourne un JWT (TokenDTO). Aucune authentification requise.")
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.login(loginDTO));
    }

    @Operation(summary = "Enregistrement d’un utilisateur", description = "Retourne un message de succès. Aucune authentification requise.")
    @PostMapping("/register")
    public ResponseEntity<MessageDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(authService.register(registerDTO));
    }

    @Operation(summary = "Enregistrement d’un utilisateur avec rôle (USER ou ADMIN)", description = "Retourne un message de succès. Requiert un rôle ADMIN.")
    @PostMapping("/admin/users")
    public ResponseEntity<MessageDTO> registerWithRole(@Valid @RequestBody RegisterWithRoleDTO dto, Authentication authentication) {
        return ResponseEntity.ok(authService.registerWithRole(dto,authentication));
    }
}
