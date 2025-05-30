package com.CESIZen.prod.controller;


import java.util.List;
import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.diagnostic.DiagnosticEventDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticHistoryDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticResultDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticSubmitDTO;
import com.CESIZen.prod.service.DiagnosticService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diagnostic")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @Operation(summary = "Voir les événements du diagnostic", description = "Accessible à tous. Aucune authentification requise.")
    @GetMapping
    public ResponseEntity<List<DiagnosticEventDTO>> getAll() {
        return ResponseEntity.ok(diagnosticService.getAllEvents());
    }

    @Operation(summary = "Historique personnel du diagnostic", description = "Requiert l'authentification (utilisateur connecté).")
    @GetMapping("/me/history")
    public ResponseEntity<List<DiagnosticHistoryDTO>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(diagnosticService.getUserHistory(authentication));
    }

    @Operation(summary = "Soumettre un diagnostic", description = "Requiert l'authentification (utilisateur connecté).")
    @PostMapping("/submit")
    public ResponseEntity<DiagnosticResultDTO> submit(@RequestBody DiagnosticSubmitDTO dto,
                                                      Authentication authentication) {
        return ResponseEntity.ok(diagnosticService.submitDiagnostic(dto, authentication));
    }

    @Operation(summary = "Créer un événement de diagnostic", description = "Requiert un rôle ADMIN.")
    @PostMapping("/admin")
    public ResponseEntity<DiagnosticEventDTO> create(@RequestBody DiagnosticEventDTO dto) {
        return ResponseEntity.ok(diagnosticService.createEvent(dto));
    }

    @Operation(summary = "Modifier un événement de diagnostic", description = "Requiert un rôle ADMIN.")
    @PutMapping("/admin/{id}")
    public ResponseEntity<DiagnosticEventDTO> update(@PathVariable Long id,
                                                     @RequestBody DiagnosticEventDTO dto) {
        return ResponseEntity.ok(diagnosticService.updateEvent(id, dto));
    }

    @Operation(summary = "Supprimer un événement de diagnostic", description = "Requiert un rôle ADMIN.")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<MessageDTO> delete(@PathVariable Long id) {
        MessageDTO messageDTO = diagnosticService.deleteEvent(id);
        return ResponseEntity.ok(messageDTO);
    }
}

