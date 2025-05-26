package com.CESIZen.prod.controller;


import java.util.List;
import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.diagnostic.DiagnosticEventDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticHistoryDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticResultDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticSubmitDTO;
import com.CESIZen.prod.service.DiagnosticService;
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

    @GetMapping
    public ResponseEntity<List<DiagnosticEventDTO>> getAll() {
        return ResponseEntity.ok(diagnosticService.getAllEvents());
    }

    @GetMapping("/me/history")
    public ResponseEntity<List<DiagnosticHistoryDTO>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(diagnosticService.getUserHistory(authentication));
    }

    @PostMapping("/submit")
    public ResponseEntity<DiagnosticResultDTO> submit(@RequestBody DiagnosticSubmitDTO dto,
                                                      Authentication authentication) {
        return ResponseEntity.ok(diagnosticService.submitDiagnostic(dto, authentication));
    }

    @PostMapping("/admin")
    public ResponseEntity<DiagnosticEventDTO> create(@RequestBody DiagnosticEventDTO dto) {
        return ResponseEntity.ok(diagnosticService.createEvent(dto));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<DiagnosticEventDTO> update(@PathVariable Long id,
                                                     @RequestBody DiagnosticEventDTO dto) {
        return ResponseEntity.ok(diagnosticService.updateEvent(id, dto));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<MessageDTO> delete(@PathVariable Long id) {
        diagnosticService.deleteEvent(id);
        return ResponseEntity.ok(new MessageDTO("Événement supprimé"));
    }
}

