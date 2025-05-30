package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.resource.CreateResourceDTO;
import com.CESIZen.prod.dto.resource.ResourceDTO;
import com.CESIZen.prod.dto.resource.UpdateResourceDTO;
import com.CESIZen.prod.entity.Resource;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<ResourceDTO> findAll() {
        log.info("Récupération de toutes les ressources");
        return resourceRepository.findAll()
                .stream()
                .map(ResourceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ResourceDTO findById(Long id) {
        log.info("Recherche de la ressource avec id={}", id);
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ressource non trouvée avec id={}", id);
                    return new NotFoundException("Ressource non trouvée");
                });
        return ResourceDTO.fromEntity(resource);
    }

    public ResourceDTO create(CreateResourceDTO dto) {
        log.info("Création d'une nouvelle ressource avec titre='{}'", dto.getTitle());
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setContent(dto.getContent());
        resource.setImageUrl(dto.getImageUrl());

        resource = resourceRepository.save(resource);
        log.info("Ressource créée avec id={}", resource.getId());
        return ResourceDTO.fromEntity(resource);
    }

    public ResourceDTO update(Long id, UpdateResourceDTO dto) {
        log.info("Mise à jour de la ressource id={}", id);
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ressource non trouvée pour mise à jour, id={}", id);
                    return new NotFoundException("Ressource non trouvée");
                });

        if (dto.getTitle() != null) resource.setTitle(dto.getTitle());
        if (dto.getContent() != null) resource.setContent(dto.getContent());
        if (dto.getImageUrl() != null) resource.setImageUrl(dto.getImageUrl());

        Resource updated = resourceRepository.save(resource);
        log.info("Ressource mise à jour avec succès, id={}", updated.getId());
        return ResourceDTO.fromEntity(updated);
    }

    public void delete(Long id) {
        log.info("Suppression de la ressource id={}", id);
        if (!resourceRepository.existsById(id)) {
            log.warn("Ressource non trouvée pour suppression, id={}", id);
            throw new NotFoundException("Ressource non trouvée");
        }
        resourceRepository.deleteById(id);
        log.info("Ressource supprimée avec succès, id={}", id);
    }
}
