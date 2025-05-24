package com.CESIZen.prod.controller;

import java.util.List;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.TokenDTO;
import com.CESIZen.prod.dto.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.CESIZen.prod.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Créer un nouvel utilisateur", description = "Permet de créer un compte utilisateur.")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès (UserDTO)")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody RegisterDTO request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Créer un nouvel utilisateur avec un rôle spécifique", description = "Permet à un SUPER_ADMIN de créer un compte USER, MODERATOR, ADMIN ou SUPER_ADMIN.")
    @ApiResponse(responseCode = "201", description = "Utilisateur avec rôle créé avec succès (UserDTO)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<UserDTO> createUserWithRole(@RequestBody RegisterWithRoleDTO request) {
        UserDTO user = userService.createUserWithRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Connexion utilisateur", description = "Permet à un utilisateur de se connecter et de recevoir un jeton JWT.")
    @ApiResponse(responseCode = "200", description = "Connexion réussie et jeton JWT retourné (TokenDTO)")
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> loginUser(@RequestBody LoginDTO request) {
        TokenDTO token = userService.loginUser(request);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Récupérer les informations de l'utilisateur connecté", description = "Retourne les données de l'utilisateur actuellement authentifié.")
    @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur connecté récupérées (UserDTO)")
    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        UserDTO user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Récupérer un utilisateur par son ID", description = "Permet aux administrateurs de récupérer les informations d'un utilisateur donné.")
    @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès (UserDTO)")
    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Récupérer tous les utilisateurs", description = "Permet aux administrateurs de récupérer la liste de tous les utilisateurs.")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée (List<UserDTO>)")
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Mettre à jour les informations de l'utilisateur connecté", description = "Permet à un utilisateur de modifier ses propres informations.")
    @ApiResponse(responseCode = "200", description = "Informations utilisateur mises à jour (UserDTO)")
    @PatchMapping("/users/me")
    public ResponseEntity<UserDTO> updateCurrentUser(Authentication authentication, @RequestBody UpdateUserDTO request) {
        UserDTO updatedUser = userService.updateCurrentUser(authentication, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Mettre à jour un utilisateur par son ID", description = "Permet aux administrateurs de modifier les informations d'un utilisateur donné.")
    @ApiResponse(responseCode = "200", description = "Informations utilisateur mises à jour (UserDTO)")
    @PatchMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO request) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Activer ou désactiver un utilisateur", description = "Permet aux administrateurs de changer le statut (actif/inactif) d’un utilisateur citoyen.")
    @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour (UserDTO)")
    @PatchMapping("/users/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserDTO> toggleUserStatus(@PathVariable Long id) {
        UserDTO updatedUser = userService.toggleUserStatus(id);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Supprimer le compte de l'utilisateur connecté", description = "Permet à un utilisateur de supprimer son propre compte.")
    @ApiResponse(responseCode = "200", description = "Compte utilisateur supprimé (MessageDTO)")
    @DeleteMapping("/users/me")
    public ResponseEntity<MessageDTO> deleteCurrentUser(Authentication authentication) {
        MessageDTO message = userService.deleteCurrentUser(authentication);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Supprimer un utilisateur par son ID", description = "Permet aux administrateurs de supprimer un compte utilisateur donné.")
    @ApiResponse(responseCode = "200", description = "Compte utilisateur supprimé (MessageDTO)")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MessageDTO> deleteUser(@PathVariable Long id) {
        MessageDTO message = userService.deleteUser(id);
        return ResponseEntity.ok(message);
    }
}
