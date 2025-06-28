package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.resource.*;
import com.CESIZen.prod.entity.Resource;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResourceServiceTest {

    @InjectMocks
    private ResourceService resourceService;

    @Mock
    private ResourceRepository resourceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllResources() {
        Resource r1 = new Resource();
        r1.setId(1L);
        r1.setTitle("Titre 1");
        r1.setContent("Contenu 1");
        r1.setImageUrl("image1.png");

        Resource r2 = new Resource();
        r2.setId(2L);
        r2.setTitle("Titre 2");
        r2.setContent("Contenu 2");
        r2.setImageUrl("image2.png");

        List<Resource> mockList = List.of(r1, r2);
        when(resourceRepository.findAll()).thenReturn(mockList);

        List<ResourceDTO> result = resourceService.findAll();

        assertEquals(2, result.size());
        assertEquals("Titre 1", result.get(0).getTitle());
        assertEquals("Titre 2", result.get(1).getTitle());
    }

    @Test
    void testFindById_Found() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setTitle("Titre");
        resource.setContent("Contenu");
        resource.setImageUrl("image.png");

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceDTO result = resourceService.findById(1L);

        assertNotNull(result);
        assertEquals("Titre", result.getTitle());
    }

    @Test
    void testFindById_NotFound() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> resourceService.findById(1L));
    }

    @Test
    void testCreateResource() {
        CreateResourceDTO dto = new CreateResourceDTO("Nouveau Titre", "Nouveau Contenu", "nouvelleImage.png");

        Resource saved = new Resource();
        saved.setId(1L);
        saved.setTitle(dto.getTitle());
        saved.setContent(dto.getContent());
        saved.setImageUrl(dto.getImageUrl());

        when(resourceRepository.save(any(Resource.class))).thenReturn(saved);

        ResourceDTO result = resourceService.create(dto);

        assertNotNull(result);
        assertEquals("Nouveau Titre", result.getTitle());
        assertEquals("nouvelleImage.png", result.getImageUrl());
    }

    @Test
    void testUpdateResource_Found() {
        Long id = 1L;
        UpdateResourceDTO dto = new UpdateResourceDTO("Titre modifié", "Contenu modifié", "imageModifiee.png");

        Resource existing = new Resource();
        existing.setId(id);
        existing.setTitle("Ancien Titre");
        existing.setContent("Ancien Contenu");
        existing.setImageUrl("ancienneImage.png");

        when(resourceRepository.findById(id)).thenReturn(Optional.of(existing));
        when(resourceRepository.save(any(Resource.class))).thenReturn(existing);

        ResourceDTO result = resourceService.update(id, dto);

        assertEquals("Titre modifié", result.getTitle());
        assertEquals("Contenu modifié", result.getContent());
        assertEquals("imageModifiee.png", result.getImageUrl());
    }

    @Test
    void testUpdateResource_NotFound() {
        Long id = 99L;
        UpdateResourceDTO dto = new UpdateResourceDTO("x", "y", "z");

        when(resourceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> resourceService.update(id, dto));
    }

    @Test
    void testDeleteResource_Found() {
        Long id = 1L;
        when(resourceRepository.existsById(id)).thenReturn(true);
        doNothing().when(resourceRepository).deleteById(id);

        assertDoesNotThrow(() -> resourceService.delete(id));
        verify(resourceRepository).deleteById(id);
    }

    @Test
    void testDeleteResource_NotFound() {
        Long id = 404L;
        when(resourceRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> resourceService.delete(id));
    }
}
