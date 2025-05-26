package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.diagnostic.DiagnosticScoreRangeDTO;
import com.CESIZen.prod.service.DiagnosticScoreRangeService;
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

    @GetMapping
    public ResponseEntity<List<DiagnosticScoreRangeDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<DiagnosticScoreRangeDTO> create(@RequestBody DiagnosticScoreRangeDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnosticScoreRangeDTO> update(@PathVariable Long id,
                                                          @RequestBody DiagnosticScoreRangeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

