package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.resource.CreateResourceDTO;
import com.CESIZen.prod.dto.resource.ResourceDTO;
import com.CESIZen.prod.dto.resource.UpdateResourceDTO;
import com.CESIZen.prod.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Operation(summary = "Voir toutes les ressources", description = "Accessible à tous")
    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAll() {
        return ResponseEntity.ok(resourceService.findAll());
    }

    @Operation(summary = "Voir une ressource par son ID", description = "Accessible à tous")
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.findById(id));
    }

    @Operation(summary = "Créer une ressource", description = "Réservé aux administrateurs")
    @PostMapping
    public ResponseEntity<ResourceDTO> create(@RequestBody CreateResourceDTO dto) {
        return ResponseEntity.ok(resourceService.create(dto));
    }

    @Operation(summary = "Modifier une ressource", description = "Réservé aux administrateurs")
    @PatchMapping("/{id}")
    public ResponseEntity<ResourceDTO> update(@PathVariable Long id,
                                              @RequestBody UpdateResourceDTO dto) {
        return ResponseEntity.ok(resourceService.update(id, dto));
    }

    @Operation(summary = "Supprimer une ressource", description = "Réservé aux administrateurs")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.ok(new MessageDTO("Ressource supprimée"));
    }
}
