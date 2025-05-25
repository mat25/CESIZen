package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.resetPassword.ResetPasswordConfirmDTO;
import com.CESIZen.prod.dto.resetPassword.ResetPasswordRequestDTO;
import com.CESIZen.prod.service.ResetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/reset-password")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    public ResetPasswordController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @Operation(summary = "Demander un lien de réinitialisation de mot de passe")
    @PostMapping("/request")
    public ResponseEntity<MessageDTO> request(@RequestBody ResetPasswordRequestDTO dto) {
        return ResponseEntity.ok(resetPasswordService.requestReset(dto));
    }

    @Operation(summary = "Réinitialiser le mot de passe avec un token")
    @PostMapping("/confirm")
    public ResponseEntity<MessageDTO> confirm(@RequestBody ResetPasswordConfirmDTO dto) {
        return ResponseEntity.ok(resetPasswordService.confirmReset(dto));
    }
}
