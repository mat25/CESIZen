package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.LoginDTO;
import com.CESIZen.prod.dto.user.RegisterDTO;
import com.CESIZen.prod.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Connexion d’un utilisateur", description = "Retourne un JWT (TokenDTO)")
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.login(loginDTO));
    }

    @Operation(summary = "Enregistrement d’un utilisateur", description = "Retourne un message de succès")
    @PostMapping("/register")
    public ResponseEntity<MessageDTO> register(@RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(authService.register(registerDTO));
    }
}
