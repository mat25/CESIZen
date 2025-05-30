package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticScoreRangeDTO;
import com.CESIZen.prod.service.DiagnosticScoreRangeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diagnostic/ranges")
public class DiagnosticRangeController {

    private final DiagnosticScoreRangeService service;

    public DiagnosticRangeController(DiagnosticScoreRangeService service) {
        this.service = service;
    }

    @Operation(summary = "Voir toutes les plages de score", description = "Requiert un rôle ADMIN.")
    @GetMapping
    public ResponseEntity<List<DiagnosticScoreRangeDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Créer une plage de score", description = "Requiert un rôle ADMIN.")
    @PostMapping
    public ResponseEntity<DiagnosticScoreRangeDTO> create(@Valid @RequestBody DiagnosticScoreRangeDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Modifier une plage de score", description = "Requiert un rôle ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<DiagnosticScoreRangeDTO> update(@PathVariable Long id,
                                                          @Valid @RequestBody DiagnosticScoreRangeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Supprimer une plage de score", description = "Requiert un rôle ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

