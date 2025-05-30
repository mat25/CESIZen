package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.user.UpdateUserDTO;
import com.CESIZen.prod.dto.user.UserDTO;
import com.CESIZen.prod.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtenir les informations de l'utilisateur connecté", description = "Retourne un UserDTO. Requiert l'authentification (utilisateur connecté).")
    @ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

    @Operation(summary = "Modifier ses informations personnelles", description = "Retourne un UserDTO mis à jour. Requiert l'authentification (utilisateur connecté).")
    @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour")
    @PatchMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(Authentication authentication, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok(userService.updateCurrentUser(authentication, updateUserDTO));
    }

    @Operation(summary = "Supprimer le compte de l'utilisateur connecté", description = "Permet à un utilisateur de supprimer son propre compte. Requiert l'authentification (utilisateur connecté).")
    @ApiResponse(responseCode = "200", description = "Compte utilisateur supprimé (MessageDTO)")
    @DeleteMapping("/me")
    public ResponseEntity<MessageDTO> deleteCurrentUser(Authentication authentication) {
        MessageDTO message = userService.deleteCurrentUser(authentication);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Télécharger ses données personnelles (RGPD)", description = "Retourne un fichier JSON contenant les données. Requiert l'authentification (utilisateur connecté).")
    @GetMapping("/me/data")
    public ResponseEntity<byte[]> downloadMyData(Authentication authentication) {
        byte[] json = userService.exportUserData(authentication);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_data.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    @Operation(summary = "Demander la suppression de ses données personnelles (RGPD)", description = "Supprime les données utilisateur. Requiert l'authentification (utilisateur connecté).")
    @DeleteMapping("/me/data")
    public ResponseEntity<MessageDTO> requestDataDeletion(Authentication authentication) {
        return ResponseEntity.ok(userService.requestDataDeletion(authentication));
    }

    @Operation(summary = "Récupérer tous les utilisateurs", description = "Retourne une liste de tous les utilisateurs ayant le role 'USER'. Requiert un rôle ADMIN.")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Désactiver un utilisateur", description = "Requiert un rôle ADMIN.")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @Operation(summary = "Réactiver un utilisateur", description = "Requiert un rôle ADMIN.")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @Operation(summary = "Supprimer un utilisateur", description = "Requiert un rôle ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUserById(id));
    }
}
