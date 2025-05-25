package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.*;
import com.CESIZen.prod.dto.resource.CreateResourceDTO;
import com.CESIZen.prod.dto.resource.ResourceDTO;
import com.CESIZen.prod.dto.resource.UpdateResourceDTO;
import com.CESIZen.prod.entity.Resource;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<ResourceDTO> findAll() {
        return resourceRepository.findAll()
                .stream()
                .map(ResourceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ResourceDTO findById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ressource non trouvée"));
        return ResourceDTO.fromEntity(resource);
    }

    public ResourceDTO create(CreateResourceDTO dto) {
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setContent(dto.getContent());
        resource.setImageUrl(dto.getImageUrl());

        resource = resourceRepository.save(resource);
        return ResourceDTO.fromEntity(resource);
    }

    public ResourceDTO update(Long id, UpdateResourceDTO dto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ressource non trouvée"));

        if (dto.getTitle() != null) resource.setTitle(dto.getTitle());
        if (dto.getContent() != null) resource.setContent(dto.getContent());
        if (dto.getImageUrl() != null) resource.setImageUrl(dto.getImageUrl());

        return ResourceDTO.fromEntity(resourceRepository.save(resource));
    }

    public void delete(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new NotFoundException("Ressource non trouvée");
        }
        resourceRepository.deleteById(id);
    }
}
